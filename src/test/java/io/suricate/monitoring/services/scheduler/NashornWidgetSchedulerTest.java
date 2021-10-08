package io.suricate.monitoring.services.scheduler;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectWidget;
import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Widget;
import io.suricate.monitoring.model.enums.WidgetStateEnum;
import io.suricate.monitoring.repositories.*;
import io.suricate.monitoring.services.api.ProjectWidgetService;
import io.suricate.monitoring.services.nashorn.services.NashornService;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.utils.FilesUtilsTest;
import io.suricate.monitoring.utils.WidgetUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.google.common.truth.Truth.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NashornWidgetSchedulerTest {

    /**
     * The scheduler scheduling the widget execution through Nashorn
     */
    @Autowired
    NashornRequestWidgetExecutionScheduler nashornWidgetScheduler;

    /**
     * Thread scheduler scheduling the asynchronous task which will execute a Nashorn request
     */
    private ScheduledThreadPoolExecutor scheduleNashornRequestExecutionThread;

    /**
     * Thread scheduler scheduling the asynchronous task which will wait for the Nashorn response
     */
    private ScheduledThreadPoolExecutor scheduleNashornRequestResponseThread;

    /**
     * For each widget instance, this map stores both Nashorn tasks : the task which will execute the widget
     * and the task which will wait for the response
     */
    private Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>> nashornTasksByProjectWidgetId = new ConcurrentHashMap<>();

    /**
     * The project widget service
     */
    @Autowired
    ProjectWidgetService projectWidgetService;

    /**
     * The Nashorn service
     */
    @Autowired
    NashornService nashornService;

    /**
     * The widget repository
     */
    @Autowired
    WidgetRepository widgetRepository;

    /**
     * The project repository
     */
    @Autowired
    ProjectRepository projectRepository;

    /**
     * The category repository
     */
    @Autowired
    CategoryRepository categoryRepository;

    /**
     * The widget configuration repository
     */
    @Autowired
    CategoryParametersRepository categoryParametersRepository;

    /**
     * The project widget repository
     */
    @Autowired
    ProjectWidgetRepository projectWidgetRepository;

    private ProjectWidget projectWidget;

    /**
     * Initialize the unit tests
     */
    @PostConstruct
    @Transactional
    public void before() throws IOException {
        this.nashornWidgetScheduler.init();

        this.scheduleNashornRequestExecutionThread = (ScheduledThreadPoolExecutor) ReflectionTestUtils
                .getField(nashornWidgetScheduler, "scheduleNashornRequestExecutionThread");

        this.scheduleNashornRequestResponseThread = (ScheduledThreadPoolExecutor) ReflectionTestUtils
                .getField(nashornWidgetScheduler, "scheduleNashornRequestResponseThread");

        this.nashornTasksByProjectWidgetId = (Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>>) ReflectionTestUtils
                .getField(nashornWidgetScheduler, "nashornTasksByProjectWidgetId");

        this.initDatabase();
    }

    @Test
    @Transactional
    public void testCancelAndSchedule() throws InterruptedException {
        assertThat(scheduleNashornRequestExecutionThread.getTaskCount()).isEqualTo(0);
        assertThat(scheduleNashornRequestResponseThread.getTaskCount()).isEqualTo(0);
        assertThat(widgetRepository.count()).isEqualTo(1);

        // Schedule widget
        NashornRequest nashornRequest = nashornService.getNashornRequestByProjectWidgetId(projectWidget.getId());
        nashornWidgetScheduler.cancelAndScheduleNashornRequest(nashornRequest);
        assertThat(scheduleNashornRequestResponseThread.getTaskCount()).isGreaterThan(0L);

        // Get running task
        WeakReference<ScheduledFuture<NashornResponse>> response = nashornTasksByProjectWidgetId.get(projectWidget.getId()).getKey();
        ScheduledFuture<NashornResponse> future = response.get();
        assertThat(future).isNotNull();

        // Reschedule widget
        nashornWidgetScheduler.cancelAndScheduleNashornRequest(nashornService.getNashornRequestByProjectWidgetId(projectWidget.getId()));

        ScheduledFuture<NashornResponse> newFuture = nashornTasksByProjectWidgetId.get(projectWidget.getId()).getKey().get();

        // Check task canceled
        assertThat(future.isCancelled()).isTrue();
        // check not the same task
        assertThat(newFuture).isNotEqualTo(future);
        Thread.sleep(2100);
        // Wait completion
        while (scheduleNashornRequestResponseThread.getActiveCount() != 0) {
        }

        Assert.assertNotNull(newFuture);
        assertThat(newFuture.isDone()).isTrue();

        // reinit
        nashornWidgetScheduler.init();
        scheduleNashornRequestExecutionThread = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(nashornWidgetScheduler, "scheduleNashornRequestExecutionThread");
        scheduleNashornRequestResponseThread = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(nashornWidgetScheduler, "scheduleNashornRequestResponseThread");
        // TODO : Check behavior randomly switch from 1 to 2
    }

    @Test
    @Transactional
    public void testNotValidRequest() throws IOException, InterruptedException {
        projectWidgetService.updateState(null, projectWidget.getId(), null);
        NashornRequest nashornRequest = nashornService.getNashornRequestByProjectWidgetId(projectWidget.getId());
        nashornRequest.setPreviousData(null);

        // Schedule widget
        nashornWidgetScheduler.cancelAndScheduleNashornRequest(nashornRequest);
        ProjectWidget current = projectWidgetService.getOne(projectWidget.getId()).get();
        assertThat(current.getState()).isEqualTo(WidgetStateEnum.STOPPED);
        assertThat(current.getLastExecutionDate()).isNotNull();
        assertThat(current.getLastSuccessDate()).isNull();
    }

    @Test
    @Transactional
    public void testBadDelay() {
        projectWidgetService.updateState(null, projectWidget.getId(), null);
    }

    /**
     * Init a mocked database for the unit tests
     */
    private void initDatabase() throws IOException {
        Project project = new Project();
        project.setName("test");
        project.setToken("999999");
        projectRepository.save(project);

        Category category = new Category();
        category.setName("Test");
        category.setTechnicalName("Test");
        categoryRepository.save(category);

        Widget widget = WidgetUtils.getWidget(new File(FilesUtilsTest.class.getResource("/widgets/test/widgets/alwaysRun").getFile()));
        widget.setCategory(category);
        widgetRepository.save(widget);

        // Add widget Instance
        projectWidget = new ProjectWidget();
        projectWidget.setState(WidgetStateEnum.STOPPED);
        projectWidget.setProject(project);
        projectWidget.setWidget(widget);
        projectWidget.setData("{}");
        projectWidgetRepository.save(projectWidget);
    }
}

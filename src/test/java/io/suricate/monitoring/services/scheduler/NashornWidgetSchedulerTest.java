package io.suricate.monitoring.services.scheduler;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.model.enums.WidgetStateEnum;
import io.suricate.monitoring.repositories.*;
import io.suricate.monitoring.services.api.ProjectWidgetService;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.nashorn.services.NashornService;
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

import static org.junit.jupiter.api.Assertions.*;

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
    private ScheduledThreadPoolExecutor nashornRequestExecutor;

    /**
     * Thread scheduler scheduling the asynchronous task which will wait for the Nashorn response
     */
    private ScheduledThreadPoolExecutor nashornRequestResponseExecutor;

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
     * The project grid repository
     */
    @Autowired
    ProjectGridRepository projectGridRepository;

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

        this.nashornRequestExecutor = (ScheduledThreadPoolExecutor) ReflectionTestUtils
                .getField(nashornWidgetScheduler, "nashornRequestExecutor");

        this.nashornRequestResponseExecutor = (ScheduledThreadPoolExecutor) ReflectionTestUtils
                .getField(nashornWidgetScheduler, "nashornRequestResponseExecutor");

        this.nashornTasksByProjectWidgetId = (Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>>) ReflectionTestUtils
                .getField(nashornWidgetScheduler, "nashornTasksByProjectWidgetId");

        this.initDatabase();
    }

    @Test
    @Transactional
    public void testCancelAndSchedule() throws InterruptedException {
        assertEquals(0, nashornRequestExecutor.getTaskCount());
        assertEquals(0, nashornRequestResponseExecutor.getTaskCount());
        assertEquals(1, widgetRepository.count());

        // Schedule widget
        NashornRequest nashornRequest = nashornService.getNashornRequestByProjectWidgetId(projectWidget.getId());
        nashornWidgetScheduler.cancelAndScheduleNashornRequest(nashornRequest);
        assertTrue(nashornRequestResponseExecutor.getTaskCount() > 0L);

        // Get running task
        WeakReference<ScheduledFuture<NashornResponse>> response = nashornTasksByProjectWidgetId.get(projectWidget.getId()).getKey();
        ScheduledFuture<NashornResponse> future = response.get();
        assertNotNull(future);

        // Reschedule widget
        nashornWidgetScheduler.cancelAndScheduleNashornRequest(nashornService.getNashornRequestByProjectWidgetId(projectWidget.getId()));

        ScheduledFuture<NashornResponse> newFuture = nashornTasksByProjectWidgetId.get(projectWidget.getId()).getKey().get();

        // Check task canceled
        assertTrue(future.isCancelled());
        // check not the same task
        assertNotEquals(newFuture, future);
        Thread.sleep(2100);
        // Wait completion
        while (nashornRequestResponseExecutor.getActiveCount() != 0) {
        }

        Assert.assertNotNull(newFuture);
        assertTrue(newFuture.isDone());

        // reinit
        nashornWidgetScheduler.init();
        nashornRequestExecutor = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(nashornWidgetScheduler, "nashornRequestExecutor");
        nashornRequestResponseExecutor = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(nashornWidgetScheduler, "nashornRequestResponseExecutor");
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
        assertEquals(WidgetStateEnum.STOPPED, current.getState());
        assertNotNull(current.getLastExecutionDate());
        assertNull(current.getLastSuccessDate());
    }

    /**
     * Init a mocked database for the unit tests
     */
    private void initDatabase() throws IOException {
        Project project = new Project();
        project.setName("test");
        project.setToken("999999");
        projectRepository.save(project);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setProject(project);
        projectGrid.setTime(10);
        projectGridRepository.save(projectGrid);

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
        projectWidget.setProjectGrid(projectGrid);
        projectWidget.setWidget(widget);
        projectWidget.setData("{}");
        projectWidgetRepository.save(projectWidget);
    }
}

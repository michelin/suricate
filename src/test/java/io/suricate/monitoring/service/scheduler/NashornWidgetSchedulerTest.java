package io.suricate.monitoring.service.scheduler;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.repository.ProjectWidgetRepository;
import io.suricate.monitoring.repository.WidgetRepository;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.nashorn.NashornService;
import io.suricate.monitoring.service.scheduler.NashornWidgetScheduler;
import io.suricate.monitoring.utils.FilesUtilsTest;
import io.suricate.monitoring.utils.WidgetUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.google.common.truth.Truth.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NashornWidgetSchedulerTest {

    @Autowired
    NashornWidgetScheduler nashornWidgetScheduler;

    @Autowired
    NashornService nashornService;

    @Autowired
    WidgetRepository widgetRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectWidgetRepository projectWidgetRepository;

    @Autowired
    ProjectWidgetService projectWidgetService;

    private ScheduledThreadPoolExecutor scheduledExecutorService;
    private ScheduledThreadPoolExecutor scheduledExecutorServiceFuture;
    private Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>> jobs;

    private ProjectWidget projectWidget;

    @PostConstruct
    @Transactional
    public void before() throws IOException {
        nashornWidgetScheduler.initScheduler();
        scheduledExecutorService = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(nashornWidgetScheduler,"scheduledExecutorService");
        scheduledExecutorServiceFuture = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(nashornWidgetScheduler,"scheduledExecutorServiceFuture");
        jobs = (Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>>) ReflectionTestUtils.getField(nashornWidgetScheduler,"jobs");

        // init database
        Project project = new Project();
        project.setName("test");
        project.setToken("999999");
        projectRepository.save(project);

        // Add widget
        Widget widget = WidgetUtils.getWidget(new File(FilesUtilsTest.class.getResource("/widgets/test/widgets/alwaysRun").getFile()));
        widgetRepository.save(widget);

        // Add widget Instance
        projectWidget = new ProjectWidget();
        projectWidget.setState(WidgetState.STOPPED);
        projectWidget.setProject(project);
        projectWidget.setWidget(widget);
        projectWidget.setData("{}");
        projectWidgetRepository.save(projectWidget);
    }

    @Test
    @Transactional
    public void testCancelAndSchedule() throws IOException, InterruptedException {
        assertThat(scheduledExecutorService.getTaskCount()).isEqualTo(0);
        assertThat(scheduledExecutorServiceFuture.getTaskCount()).isEqualTo(0);
        assertThat(widgetRepository.count()).isEqualTo(1);

        // Schedule widget
        NashornRequest nashornRequest = nashornService.getNashornRequestByProjectWidgetId(projectWidget.getId());
        nashornWidgetScheduler.cancelAndSchedule(nashornRequest);
        // TODO : Check behavior randomly switch from 1 to 2
        assertThat(scheduledExecutorServiceFuture.getTaskCount()).isGreaterThan(0L);

        // Get running task
        WeakReference<ScheduledFuture<NashornResponse>> response = jobs.get(projectWidget.getId()).getKey();
        ScheduledFuture<NashornResponse> future = response.get();
        assertThat(future).isNotNull();

        // Reschedule widget
        nashornWidgetScheduler.cancelAndSchedule(nashornService.getNashornRequestByProjectWidgetId(projectWidget.getId()));

        ScheduledFuture<NashornResponse> newFuture = jobs.get(projectWidget.getId()).getKey().get();

        // Check task canceled
        assertThat(future.isCancelled()).isTrue();
        // check not the same task
        assertThat(newFuture).isNotEqualTo(future);
        Thread.sleep(2100);
        // Wait completion
        while(scheduledExecutorServiceFuture.getActiveCount() != 0){}
        Assert.assertNotNull(newFuture);
        assertThat(newFuture.isDone()).isTrue();

        // reinit
        nashornWidgetScheduler.initScheduler();
        scheduledExecutorService = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(nashornWidgetScheduler,"scheduledExecutorService");
        scheduledExecutorServiceFuture = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(nashornWidgetScheduler,"scheduledExecutorServiceFuture");
        // TODO : Check behavior randomly switch from 1 to 2
    }

    @Test
    @Transactional
    public void testNotValidRequest() throws IOException, InterruptedException {
        projectWidgetService.updateState(null, projectWidget.getId(), null);
        NashornRequest nashornRequest = nashornService.getNashornRequestByProjectWidgetId(projectWidget.getId());
        nashornRequest.setPreviousData(null);

        // Schedule widget
        nashornWidgetScheduler.cancelAndSchedule(nashornRequest);
        ProjectWidget current = projectWidgetService.getOne(projectWidget.getId());
        assertThat(current.getState()).isEqualTo(WidgetState.STOPPED);
        assertThat(current.getLastExecutionDate()).isNotNull();
        assertThat(current.getLastSuccessDate()).isNull();
    }

    @Test
    @Transactional
    public void testBadDelay() throws IOException, InterruptedException {
        projectWidgetService.updateState(null, projectWidget.getId(), null);
        NashornRequest nashornRequest = nashornService.getNashornRequestByProjectWidgetId(projectWidget.getId());
        nashornRequest.setDelay(-1L);
        // Schedule widget
        nashornWidgetScheduler.cancelAndSchedule(nashornRequest);
        ProjectWidget current = projectWidgetService.getOne(projectWidget.getId());
        assertThat(current.getState()).isEqualTo(WidgetState.STOPPED);
        assertThat(current.getLastExecutionDate()).isNotNull();
        assertThat(current.getLastSuccessDate()).isNull();
    }
}

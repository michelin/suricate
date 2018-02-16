package io.suricate.monitoring.service;

import io.suricate.monitoring.model.entity.ProjectWidget;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.repository.ProjectWidgetRepository;
import io.suricate.monitoring.repository.WidgetRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.google.common.truth.Truth.assertThat;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WidgetExecutorTest {

    @Autowired
    WidgetExecutor widgetExecutor;

    @Autowired
    WidgetRepository widgetRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectWidgetRepository projectWidgetRepository;

    private ScheduledThreadPoolExecutor scheduledExecutorService;
    private ScheduledThreadPoolExecutor scheduledExecutorServiceFuture;
    private Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>> jobs;

    private ProjectWidget projectWidget;

//    @PostConstruct
//    @Transactional
//    public void before() throws IOException {
//        widgetExecutor.initScheduler();
//        scheduledExecutorService = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(widgetExecutor,"scheduledExecutorService");
//        scheduledExecutorServiceFuture = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(widgetExecutor,"scheduledExecutorServiceFuture");
//        jobs = (Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>>) ReflectionTestUtils.getField(widgetExecutor,"jobs");
//
//        // init database
//        Project project = new Project();
//        project.setName("test");
//        project.setToken("999999");
//        projectRepository.save(project);
//
//        // Add widget
//        Widget widget = WidgetUtils.getWidget(new File(FilesUtilsTest.class.getResource("/widgets/test/widgets/alwaysRun").getFile()));
//        widgetRepository.save(widget);
//
//        // Add widget Instance
//        projectWidget = new ProjectWidget();
//        projectWidget.setState(WidgetState.STOPPED);
//        projectWidget.setProject(project);
//        projectWidget.setWidget(widget);
//        projectWidget.setData("{}");
//        projectWidgetRepository.save(projectWidget);
//    }
//
//    @Test
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
//    public void testCancelAndSchedule() throws IOException, InterruptedException {
//        assertThat(scheduledExecutorService.getTaskCount()).isEqualTo(0);
//        assertThat(scheduledExecutorServiceFuture.getTaskCount()).isEqualTo(0);
//        assertThat(widgetRepository.count()).isEqualTo(1);
//
//        // Schedule widget
//        widgetExecutor.cancelAndSchedule(projectWidgetRepository.getRequestByProjectWidgetId(projectWidget.getId()));
//        // TODO : Check behavior randomly switch from 1 to 2
//        // assertThat(scheduledExecutorService.getTaskCount()).isEqualTo(2);
//        assertThat(scheduledExecutorServiceFuture.getTaskCount()).isGreaterThan(0L);
//
//        // Get running task
//        WeakReference<ScheduledFuture<NashornResponse>> response = jobs.get(projectWidget.getId()).getKey();
//        ScheduledFuture<NashornResponse> future = response.get();
//        assertThat(future).isNotNull();
//
//        // Reschedule widget
//        widgetExecutor.cancelAndSchedule(projectWidgetRepository.getRequestByProjectWidgetId(projectWidget.getId()));
//
//        ScheduledFuture<NashornResponse> newFuture = jobs.get(projectWidget.getId()).getKey().get();
//
//        // Check task canceled
//        assertThat(future.isCancelled()).isTrue();
//        // check not the same task
//        assertThat(newFuture).isNotEqualTo(future);
//        Thread.sleep(2100);
//        // Wait completion
//        while(scheduledExecutorServiceFuture.getActiveCount() != 0){}
//        assertThat(newFuture.isDone()).isTrue();
//
//        // reinit
//        widgetExecutor.initScheduler();
//        scheduledExecutorService = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(widgetExecutor,"scheduledExecutorService");
//        scheduledExecutorServiceFuture = (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(widgetExecutor,"scheduledExecutorServiceFuture");
//        // TODO : Check behavior randomly switch from 1 to 2
//        // assertThat(scheduledExecutorService.getTaskCount()).isEqualTo(1);
//    }
//
//    @Test
//    @Transactional
//    public void testNotValidRequest() throws IOException, InterruptedException {
//        projectWidgetRepository.updateState(null, projectWidget.getId(), null);
//        NashornRequest nashornRequest = projectWidgetRepository.getRequestByProjectWidgetId(projectWidget.getId());
//        nashornRequest.setPreviousData(null);
//        // Schedule widget
//        widgetExecutor.cancelAndSchedule(nashornRequest);
//        ProjectWidget current = projectWidgetRepository.findOne(projectWidget.getId());
//        assertThat(current.getState()).isEqualTo(WidgetState.STOPPED);
//        assertThat(current.getLastExecutionDate()).isNotNull();
//        assertThat(current.getLastSuccessDate()).isNull();
//    }
//
//    @Test
//    @Transactional
//    public void testBadDelay() throws IOException, InterruptedException {
//        projectWidgetRepository.updateState(null, projectWidget.getId(), null);
//        NashornRequest nashornRequest = projectWidgetRepository.getRequestByProjectWidgetId(projectWidget.getId());
//        nashornRequest.setDelay(-1L);
//        // Schedule widget
//        widgetExecutor.cancelAndSchedule(nashornRequest);
//        ProjectWidget current = projectWidgetRepository.findOne(projectWidget.getId());
//        assertThat(current.getState()).isEqualTo(WidgetState.STOPPED);
//        assertThat(current.getLastExecutionDate()).isNotNull();
//        assertThat(current.getLastSuccessDate()).isNull();
//    }
}

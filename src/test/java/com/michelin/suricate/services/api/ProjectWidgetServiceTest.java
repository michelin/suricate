package com.michelin.suricate.services.api;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetPositionRequestDto;
import com.michelin.suricate.model.dto.websocket.UpdateEvent;
import com.michelin.suricate.model.entities.*;
import com.michelin.suricate.model.enums.DataTypeEnum;
import com.michelin.suricate.model.enums.UpdateType;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import com.michelin.suricate.repositories.ProjectWidgetRepository;
import com.michelin.suricate.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import com.michelin.suricate.services.nashorn.services.DashboardScheduleService;
import com.michelin.suricate.services.websocket.DashboardWebSocketService;
import org.assertj.core.api.Assertions;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.io.StringReader;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectWidgetServiceTest {
    @Mock
    private ProjectWidgetRepository projectWidgetRepository;

    @Mock
    private DashboardWebSocketService dashboardWebsocketService;

    @Mock
    private DashboardScheduleService dashboardScheduleService;

    @Mock
    private WidgetService widgetService;

    @Mock
    private MustacheFactory mustacheFactory;

    @Mock
    private ApplicationContext ctx;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private NashornRequestWidgetExecutionScheduler nashornRequestWidgetExecutionScheduler;

    @InjectMocks
    private ProjectWidgetService projectWidgetService;

    @Test
    void shouldGetAll() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        when(projectWidgetRepository.findAll())
                .thenReturn(Collections.singletonList(projectWidget));

        List<ProjectWidget> actual = projectWidgetService.getAll();

        assertThat(actual)
                .isNotEmpty()
                .contains(projectWidget);

        verify(projectWidgetRepository, times(1))
                .findAll();
    }

    @Test
    void shouldGetOne() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        when(projectWidgetRepository.findById(any()))
                .thenReturn(Optional.of(projectWidget));

        Optional<ProjectWidget> actual = projectWidgetService.getOne(1L);

        assertThat(actual)
                .isNotEmpty()
                .contains(projectWidget);

        verify(projectWidgetRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldFindByIdAndProjectGridId() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        when(projectWidgetRepository.findByIdAndProjectGridId(any(), any()))
                .thenReturn(Optional.of(projectWidget));

        Optional<ProjectWidget> actual = projectWidgetService.findByIdAndProjectGridId(1L, 1L);

        assertThat(actual)
                .isNotEmpty()
                .contains(projectWidget);

        verify(projectWidgetRepository, times(1))
                .findByIdAndProjectGridId(1L, 1L);
    }

    @Test
    void shouldCreate() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setName("param");
        widgetParam.setType(DataTypeEnum.TEXT);

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");
        projectWidget.setWidget(widget);

        when(widgetService.getWidgetParametersWithCategoryParameters(any()))
                .thenReturn(Collections.singletonList(widgetParam));
        when(projectWidgetRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        ProjectWidget actual = projectWidgetService.create(projectWidget);

        assertThat(actual)
                .isEqualTo(projectWidget);

        verify(widgetService, times(1))
                .getWidgetParametersWithCategoryParameters(widget);
        verify(projectWidgetRepository, times(1))
                .save(projectWidget);
    }

    @Test
    void shouldCreateAndRefreshDashboards() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");
        projectWidget.setWidget(widget);
        projectWidget.setProjectGrid(projectGrid);

        when(projectWidgetRepository.saveAndFlush(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        doNothing().when(dashboardWebsocketService)
                .sendEventToProjectSubscribers(any(), any());

        projectWidgetService.createAndRefreshDashboards(projectWidget);

        verify(projectWidgetRepository, times(1))
                .saveAndFlush(projectWidget);
        verify(dashboardWebsocketService, times(1))
                .sendEventToProjectSubscribers("token", UpdateEvent.builder()
                        .type(UpdateType.REFRESH_DASHBOARD)
                        .build());
    }

    @Test
    void shouldUpdateWidgetPositionByProjectWidgetId() {
        doNothing().when(projectWidgetRepository)
                .updateRowAndColAndWidthAndHeightById(1, 1, 1, 1, 1L);

        projectWidgetService.updateWidgetPositionByProjectWidgetId(1L, 1, 1, 1, 1);

        verify(projectWidgetRepository, times(1))
                .updateRowAndColAndWidthAndHeightById(1, 1, 1, 1, 1L);
    }

    @Test
    void shouldUpdateWidgetPositionByProject() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        ProjectWidgetPositionRequestDto projectWidgetPositionRequestDto = new ProjectWidgetPositionRequestDto();
        projectWidgetPositionRequestDto.setProjectWidgetId(1L);
        projectWidgetPositionRequestDto.setHeight(1);
        projectWidgetPositionRequestDto.setWidth(1);
        projectWidgetPositionRequestDto.setGridColumn(1);
        projectWidgetPositionRequestDto.setGridRow(1);

        doNothing().when(projectWidgetRepository)
                .flush();
        doNothing().when(dashboardWebsocketService)
                .sendEventToProjectSubscribers(any(), any());

        projectWidgetService.updateWidgetPositionByProject(project, Collections.singletonList(projectWidgetPositionRequestDto));

        verify(projectWidgetRepository, times(1))
                .flush();
        verify(dashboardWebsocketService, times(1))
                .sendEventToProjectSubscribers("token", UpdateEvent.builder()
                        .type(UpdateType.REFRESH_DASHBOARD)
                        .build());
    }

    @Test
    void shouldRemoveWidgetFromDashboard() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");
        projectWidget.setWidget(widget);
        projectWidget.setProjectGrid(projectGrid);

        when(ctx.getBean(NashornRequestWidgetExecutionScheduler.class))
                .thenReturn(nashornRequestWidgetExecutionScheduler);
        doNothing().when(nashornRequestWidgetExecutionScheduler)
                .cancelWidgetExecution(any());
        doNothing().when(projectWidgetRepository)
                .deleteById(any());
        doNothing().when(projectWidgetRepository)
                .flush();
        doNothing().when(dashboardWebsocketService)
                .sendEventToProjectSubscribers(any(), any());
        when(projectWidgetRepository.findById(any()))
                .thenReturn(Optional.of(projectWidget));

        projectWidgetService.removeWidgetFromDashboard(1L);

        verify(nashornRequestWidgetExecutionScheduler, times(1))
                .cancelWidgetExecution(1L);
        verify(projectWidgetRepository, times(1))
                .deleteById(1L);
        verify(projectWidgetRepository, times(1))
                .flush();
        verify(dashboardWebsocketService, times(1))
                .sendEventToProjectSubscribers("token", UpdateEvent.builder()
                        .type(UpdateType.REFRESH_DASHBOARD)
                        .build());
        verify(projectWidgetRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldRemoveWidgetFromDashboardWhenWidgetNotFound() {
        when(projectWidgetRepository.findById(any()))
                .thenReturn(Optional.empty());

        projectWidgetService.removeWidgetFromDashboard(1L);

        verify(nashornRequestWidgetExecutionScheduler, times(0))
                .cancelWidgetExecution(any());
        verify(projectWidgetRepository, times(0))
                .deleteById(any());
        verify(projectWidgetRepository, times(0))
                .flush();
        verify(dashboardWebsocketService, times(0))
                .sendEventToProjectSubscribers(any(), any());
        verify(projectWidgetRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldResetProjectWidgetsState() {
        doNothing().when(projectWidgetRepository)
                .resetProjectWidgetsState();

        projectWidgetService.resetProjectWidgetsState();

        verify(projectWidgetRepository, times(1))
                .resetProjectWidgetsState();
    }

    @Test
    void shouldUpdateStateNoDate() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");

        when(projectWidgetRepository.findById(any()))
                .thenReturn(Optional.of(projectWidget));
        when(projectWidgetRepository.saveAndFlush(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        projectWidgetService.updateState(WidgetStateEnum.STOPPED, 1L);

        Assertions.assertThat(projectWidget.getState())
                .isEqualTo(WidgetStateEnum.STOPPED);

        verify(projectWidgetRepository, times(1))
                .findById(1L);
        verify(projectWidgetRepository, times(1))
                .saveAndFlush(projectWidget);
    }

    @Test
    void shouldUpdateStateWhenWidgetNotFound() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");

        when(projectWidgetRepository.findById(any()))
                .thenReturn(Optional.empty());

        projectWidgetService.updateState(WidgetStateEnum.STOPPED, 1L);

        verify(projectWidgetRepository, times(1))
                .findById(1L);
        verify(projectWidgetRepository, times(0))
                .saveAndFlush(any());
    }

    @Test
    void shouldUpdateState() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");

        when(projectWidgetRepository.findById(any()))
                .thenReturn(Optional.of(projectWidget));

        Date now = Date.from(Instant.parse("2000-01-01T01:00:00.00Z"));
        projectWidgetService.updateState(WidgetStateEnum.STOPPED, 1L, now);

        Assertions.assertThat(projectWidget.getState())
                .isEqualTo(WidgetStateEnum.STOPPED);
        assertThat(projectWidget.getLastExecutionDate())
                .isEqualTo(now);

        verify(projectWidgetRepository, times(1))
                .findById(1L);
        verify(projectWidgetRepository, times(1))
                .saveAndFlush(projectWidget);
    }

    @Test
    void shouldInstantiateProjectWidgetHtmlNoData() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);

        Widget widget = new Widget();
        widget.setId(1L);
        widget.setHtmlContent("<h1>Titre</h1>");
        widget.setWidgetParams(Collections.singleton(widgetParam));

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");
        projectWidget.setWidget(widget);

        String actual = projectWidgetService.instantiateProjectWidgetHtml(projectWidget);

        assertThat(actual)
                .isEqualTo("<h1>Titre</h1>");
    }

    @Test
    void shouldInstantiateProjectWidgetHtml() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setType(DataTypeEnum.TEXT);

        Widget widget = new Widget();
        widget.setId(1L);
        widget.setHtmlContent("<h1>{{DATA}}</h1>");
        widget.setTechnicalName("technicalName");
        widget.setWidgetParams(Collections.singleton(widgetParam));

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");
        projectWidget.setData("{\"DATA\": \"titre\"}");
        projectWidget.setWidget(widget);

        when(mustacheFactory.compile(any(), any()))
                .thenReturn(new DefaultMustacheFactory().compile(new StringReader(widget.getHtmlContent()), widget.getTechnicalName()));
        when(widgetService.getWidgetParametersWithCategoryParameters(any()))
                .thenReturn(Collections.singletonList(widgetParam));

        String actual = projectWidgetService.instantiateProjectWidgetHtml(projectWidget);

        assertThat(actual)
                .isEqualTo("<h1>titre</h1>");

        verify(widgetService, times(1))
                .getWidgetParametersWithCategoryParameters(widget);
    }

    @Test
    void shouldThrowMustacheExceptionWhenInstantiateProjectWidgetHtml() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setType(DataTypeEnum.TEXT);

        Widget widget = new Widget();
        widget.setId(1L);
        widget.setHtmlContent("<h1>{{DATA}}</h1>");
        widget.setTechnicalName("technicalName");
        widget.setWidgetParams(Collections.singleton(widgetParam));

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");
        projectWidget.setData("{\"DATA\": \"titre\"}");
        projectWidget.setWidget(widget);

        when(mustacheFactory.compile(any(), any()))
                .thenThrow(new MustacheException("Error"));
        when(widgetService.getWidgetParametersWithCategoryParameters(any()))
                .thenReturn(Collections.singletonList(widgetParam));

        String actual = projectWidgetService.instantiateProjectWidgetHtml(projectWidget);

        assertThat(actual)
                .isEmpty();

        verify(widgetService, times(1))
                .getWidgetParametersWithCategoryParameters(widget);
    }

    @Test
    void shouldThrowExceptionFromDataWhenInstantiateProjectWidgetHtml() {
        Widget widget = new Widget();
        widget.setId(1L);
        widget.setHtmlContent("<h1>{{DATA}}</h1>");
        widget.setTechnicalName("technicalName");

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setBackendConfig("param=value");
        projectWidget.setData("parseError");
        projectWidget.setWidget(widget);

        when(mustacheFactory.compile(any(), any()))
                .thenReturn(new DefaultMustacheFactory().compile(new StringReader(widget.getHtmlContent()), widget.getTechnicalName()));

        String actual = projectWidgetService.instantiateProjectWidgetHtml(projectWidget);

        assertThat(actual)
                .isEqualTo("<h1></h1>");

        verify(widgetService, times(0))
                .getWidgetParametersWithCategoryParameters(any());
    }

    @Test
    void shouldUpdateProjectWidget() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setType(DataTypeEnum.TEXT);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        when(ctx.getBean(NashornRequestWidgetExecutionScheduler.class))
                .thenReturn(nashornRequestWidgetExecutionScheduler);
        doNothing().when(nashornRequestWidgetExecutionScheduler)
                .cancelWidgetExecution(any());
        doNothing().when(dashboardScheduleService)
                .scheduleWidget(any());
        when(widgetService.getWidgetParametersWithCategoryParameters(any()))
                .thenReturn(Collections.singletonList(widgetParam));
        when(projectWidgetRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        projectWidgetService.updateProjectWidget(projectWidget, "style", "param=value");

        assertThat(projectWidget.getCustomStyle())
                .isEqualTo("style");
        assertThat(projectWidget.getBackendConfig())
                .isEqualTo("param=value");

        verify(nashornRequestWidgetExecutionScheduler, times(1))
                .cancelWidgetExecution(1L);
        verify(projectWidgetRepository, times(1))
                .save(projectWidget);
        verify(dashboardScheduleService, times(1))
                .scheduleWidget(1L);
    }

    @Test
    void shouldUpdateWidgetInstanceAfterFailedExecution() {
        doNothing().when(projectWidgetRepository)
                .updateLastExecutionDateAndStateAndLog(any(), any(), any(), any());

        Date now = new Date();
        projectWidgetService.updateWidgetInstanceAfterFailedExecution(now, "log", 1L, WidgetStateEnum.STOPPED);

        verify(projectWidgetRepository, times(1))
                .updateLastExecutionDateAndStateAndLog(now, "log", 1L, WidgetStateEnum.STOPPED);
    }

    @Test
    void shouldUpdateWidgetInstanceAfterSucceededExecution() {
        doNothing().when(projectWidgetRepository)
                .updateSuccessExecution(any(), any(), any(), any(), any());

        Date now = new Date();
        projectWidgetService.updateWidgetInstanceAfterSucceededExecution(now, "log", "data", 1L, WidgetStateEnum.STOPPED);

        verify(projectWidgetRepository, times(1))
                .updateSuccessExecution(now, "log", "data", 1L, WidgetStateEnum.STOPPED);
    }

    @Test
    void shouldDecryptSecretParamsIfNeeded() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setType(DataTypeEnum.TEXT);

        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetService.getWidgetParametersWithCategoryParameters(any()))
                .thenReturn(Collections.singletonList(widgetParam));

        String actual = projectWidgetService.decryptSecretParamsIfNeeded(widget, "param=value");

        assertThat(actual)
                .isEqualTo("param=value");

        verify(widgetService, times(1))
                .getWidgetParametersWithCategoryParameters(widget);
    }

    @Test
    void shouldDecryptSecretParamsWithPasswordIfNeeded() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setName("param");
        widgetParam.setType(DataTypeEnum.PASSWORD);

        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetService.getWidgetParametersWithCategoryParameters(any()))
                .thenReturn(Collections.singletonList(widgetParam));
        when(stringEncryptor.decrypt(any()))
                .thenReturn("decrypted");

        String actual = projectWidgetService.decryptSecretParamsIfNeeded(widget, "param=value");

        assertThat(actual)
                .isEqualTo("param=decrypted");

        verify(widgetService, times(1))
                .getWidgetParametersWithCategoryParameters(widget);
        verify(stringEncryptor, times(1))
                .decrypt("value");
    }

    @Test
    void shouldEncryptSecretParamsIfNeeded() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setType(DataTypeEnum.TEXT);

        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetService.getWidgetParametersWithCategoryParameters(any()))
                .thenReturn(Collections.singletonList(widgetParam));

        String actual = projectWidgetService.encryptSecretParamsIfNeeded(widget, "param=value");

        assertThat(actual)
                .isEqualTo("param=value");

        verify(widgetService, times(1))
                .getWidgetParametersWithCategoryParameters(widget);
    }

    @Test
    void shouldEncryptSecretParamsWithPasswordIfNeeded() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setName("param");
        widgetParam.setType(DataTypeEnum.PASSWORD);

        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetService.getWidgetParametersWithCategoryParameters(any()))
                .thenReturn(Collections.singletonList(widgetParam));
        when(stringEncryptor.encrypt(any()))
                .thenReturn("encrypted");

        String actual = projectWidgetService.encryptSecretParamsIfNeeded(widget, "param=value");

        assertThat(actual)
                .isEqualTo("param=encrypted");

        verify(widgetService, times(1))
                .getWidgetParametersWithCategoryParameters(widget);
        verify(stringEncryptor, times(1))
                .encrypt("value");
    }
}

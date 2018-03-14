package io.suricate.monitoring.service.nashorn;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.repository.ProjectWidgetRepository;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NashornService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NashornService.class.getName());

    private final ProjectWidgetService projectWidgetService;

    public NashornService(final ProjectWidgetService projectWidgetService) {
        this.projectWidgetService = projectWidgetService;
    }

    public List<NashornRequest> getEveryNashornRequestFromDatabase() {
        return projectWidgetService
            .getAll()
            .stream()
            .map(projectWidget -> {
                String properties = projectWidget.getBackendConfig();
                String script = projectWidget.getWidget().getBackendJs();
                String previousData = projectWidget.getData();
                Long projectId = projectWidget.getProject().getId();
                Long technicalId = projectWidget.getId();
                Long delay = projectWidget.getWidget().getDelay();
                Long timeout = projectWidget.getWidget().getTimeout();
                WidgetState state = projectWidget.getState();
                Date lastSuccess = projectWidget.getLastSuccessDate();

                return new NashornRequest(properties, script, previousData, projectId, technicalId, delay, timeout, state, lastSuccess);
            })
            .collect(Collectors.toList());
    }

    public boolean isNashornRequestExecutable(final NashornRequest nashornRequest) {
        if(!nashornRequest.isValid()) {
            LOGGER.debug("Widget content not isValid for widget instance :{}", nashornRequest.getProjectWidgetId());
            return false;
        }

        if (nashornRequest.getDelay() < 0) {
            LOGGER.debug("Stop widget instance because delay < 0 for projectWidgetId:{}", nashornRequest.getProjectWidgetId());
            return false;
        }

        return true;
    }

    public NashornRequest injectWidgetsConfigurations(NashornRequest nashornRequest, List<Configuration> configurations) {

        if (configurations != null && !configurations.isEmpty()) {
            StringBuilder builder = new StringBuilder(nashornRequest.getProperties()).append('\n');

            for (Configuration configuration : configurations) {
                builder
                    .append(configuration.getKey())
                    .append('=')
                    .append(configuration.getValue())
                    .append('\n');
            }
            nashornRequest.setProperties(builder.toString());
        }

        return nashornRequest;
    }
}

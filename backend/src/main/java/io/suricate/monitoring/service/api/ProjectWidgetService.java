package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.repository.ProjectWidgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class ProjectWidgetService {

    private final ProjectWidgetRepository projectWidgetRepository;

    @Autowired
    public ProjectWidgetService(final ProjectWidgetRepository projectWidgetRepository) {
        this.projectWidgetRepository = projectWidgetRepository;
    }

    public void resetProjectWidgetsState() {
        this.projectWidgetRepository.resetProjectWidgetsState();
    }

    /**
     * Method used to update application state
     * @param widgetState widget state
     * @param id project widget id
     */
    @Transactional
    public void updateState(WidgetState widgetState, Long id, Date date){
        projectWidgetRepository.updateState(widgetState, id, date);
    }
}

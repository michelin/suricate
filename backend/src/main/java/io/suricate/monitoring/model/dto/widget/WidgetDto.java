package io.suricate.monitoring.model.dto.widget;

import io.suricate.monitoring.model.dto.AbstractDto;
import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.Library;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Object representing a widget used for communication with clients of the webservice
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class WidgetDto extends AbstractDto {

    /**
     * The widget ID
     */
    private Long id;

    /**
     * The widget name
     */
    private String name;

    /**
     * The widget description
     */
    private String description;

    /**
     * The technical name
     */
    private String technicalName;

    /**
     * The html content of the widget
     */
    private String htmlContent;

    /**
     * The css content of the widget
     */
    private String cssContent;

    /**
     * The JS of the widget (executed by nashorn
     */
    private String backendJs;

    /**
     * Informations on the usage of this widget
     */
    private String info;

    /**
     * The delay for each execution of this widget
     */
    private Long delay;

    /**
     * The timeout of the nashorn execution
     */
    private Long timeout;

    /**
     * A representation by an image of the widget
     */
    private Asset image;

    /**
     * The list of related librairies
     */
    private List<Library> libraries = new ArrayList<>();

    /**
     * The category of this widget
     */
    private CategoryDto category;

    /**
     * The widget availability {@link WidgetAvailabilityEnum}
     */
    private WidgetAvailabilityEnum widgetAvailability;

    /**
     * The list of the params for this widget
     */
    private List<WidgetParamDto> widgetParams = new ArrayList<>();
}

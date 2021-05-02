package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.category.CategoryParameterResponseDto;
import io.suricate.monitoring.model.dto.api.widgetconfiguration.WidgetConfigurationResponseDto;
import io.suricate.monitoring.model.entities.CategoryParameter;
import org.jasypt.encryption.StringEncryptor;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Abstract class that manage the generation DTO/Model objects for the category parameter class
 */
@Component
@Mapper(componentModel = "spring",
        uses = {
            CategoryMapper.class
        }
)
public abstract class CategoryParamMapper {

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    StringEncryptor stringEncryptor;

    /**
     * Map a category parameter into a DTO
     *
     * @param categoryParameter The category parameter to map
     * @return The category parameter as DTO
     */
    @Named("toCategoryParameterDTO")
    @Mapping(target = "category", qualifiedByName = "toCategoryDTO")
    @Mapping(target = "value", expression = "java(" +
            "categoryParameter.getDataType() == io.suricate.monitoring.model.enums.DataTypeEnum.PASSWORD ? stringEncryptor.decrypt(categoryParameter.getValue()) : categoryParameter.getValue())" +
            "")
    public abstract CategoryParameterResponseDto toCategoryParameterDTO(CategoryParameter categoryParameter);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Map a list of category parameters into a list of DTO
     *
     * @param categoryParameters The category parameters to map
     * @return The category parameters as DTO
     */
    @Named("toCategoryParametersDTOs")
    @IterableMapping(qualifiedByName = "toCategoryParameterDTO")
    public abstract List<CategoryParameterResponseDto> toCategoryParametersDTOs(List<CategoryParameter> categoryParameters);
}

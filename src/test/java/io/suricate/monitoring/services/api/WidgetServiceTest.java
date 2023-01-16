package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.api.widget.WidgetRequestDto;
import io.suricate.monitoring.model.dto.nashorn.WidgetVariableResponse;
import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.suricate.monitoring.repositories.WidgetParamRepository;
import io.suricate.monitoring.repositories.WidgetRepository;
import io.suricate.monitoring.services.specifications.WidgetSearchSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WidgetServiceTest {
    @Mock
    private AssetService assetService;

    @Mock
    private WidgetRepository widgetRepository;

    @Mock
    private WidgetParamRepository widgetParamRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private WidgetService widgetService;

    @Test
    void shouldFindOne() {
        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetRepository.findById(any()))
                .thenReturn(Optional.of(widget));

        Optional<Widget> actual = widgetService.findOne(1L);
        assertThat(actual)
                .isPresent()
                .contains(widget);

        verify(widgetRepository, times(1))
                .findById(1L);
    }


    @Test
    void shouldFindOneByTechnicalName() {
        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetRepository.findByTechnicalName(any()))
                .thenReturn(Optional.of(widget));

        Optional<Widget> actual = widgetService.findOneByTechnicalName("technicalName");

        assertThat(actual)
                .isPresent()
                .contains(widget);

        verify(widgetRepository, times(1))
                .findByTechnicalName("technicalName");
    }

    @Test
    void shouldGetAll() {
        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetRepository.findAll(any(WidgetSearchSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(widget)));

        Page<Widget> actual = widgetService.getAll("search", Pageable.unpaged());

        assertThat(actual)
                .isNotEmpty()
                .contains(widget);

        verify(widgetRepository, times(1))
                .findAll(Mockito.<WidgetSearchSpecification>argThat(specification -> specification.getSearch().equals("search") &&
                                specification.getAttributes().isEmpty()),
                        Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldGetWidgetsByCategory() {
        Widget widget = new Widget();
        widget.setId(1L);
        List<Widget> widgets = Collections.singletonList(widget);

        when(widgetRepository.findAllByCategoryIdOrderByNameAsc(any()))
                .thenReturn(widgets);

        Optional<List<Widget>> actual = widgetService.getWidgetsByCategory(1L);

        assertThat(actual)
                .isPresent();
        assertThat(actual.get())
                .isNotEmpty()
                .contains(widget);

        verify(widgetRepository, times(1))
                .findAllByCategoryIdOrderByNameAsc(1L);
    }

    @Test
    void shouldGetWidgetsByCategoryEmptyList() {
        when(widgetRepository.findAllByCategoryIdOrderByNameAsc(any()))
                .thenReturn(Collections.emptyList());

        Optional<List<Widget>> actual = widgetService.getWidgetsByCategory(1L);

        assertThat(actual)
                .isEmpty();

        verify(widgetRepository, times(1))
                .findAllByCategoryIdOrderByNameAsc(1L);
    }

    @Test
    void shouldGetWidgetsByCategoryNull() {
        when(widgetRepository.findAllByCategoryIdOrderByNameAsc(any()))
                .thenReturn(null);

        Optional<List<Widget>> actual = widgetService.getWidgetsByCategory(1L);

        assertThat(actual)
                .isEmpty();

        verify(widgetRepository, times(1))
                .findAllByCategoryIdOrderByNameAsc(1L);
    }

    @Test
    void shouldGetWidgetParametersWithCategoryParameters() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        List<WidgetParam> widgetParams = Collections.singletonList(widgetParam);

        WidgetParam widgetParamTwo = new WidgetParam();
        widgetParam.setId(2L);

        Widget widget = new Widget();
        widget.setId(1L);
        widget.setWidgetParams(Collections.singletonList(widgetParamTwo));

        when(categoryService.getCategoryParametersByWidget(any()))
                .thenReturn(widgetParams);

        List<WidgetParam> actual = widgetService.getWidgetParametersWithCategoryParameters(widget);

        assertThat(actual)
                .hasSize(2)
                .containsExactlyInAnyOrder(widgetParam, widgetParamTwo);

        verify(categoryService, times(1))
                .getCategoryParametersByWidget(widget);
    }

    @Test
    void shouldGetWidgetParametersForNashorn() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setName("Name1");
        widgetParam.setDescription("Description");
        widgetParam.setType(DataTypeEnum.TEXT);
        widgetParam.setDefaultValue("defaultValue");

        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        WidgetParam widgetParamTwo = new WidgetParam();
        widgetParamTwo.setId(2L);
        widgetParamTwo.setName("Name2");
        widgetParamTwo.setDescription("Description");
        widgetParamTwo.setType(DataTypeEnum.COMBO);
        widgetParamTwo.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        WidgetParam widgetParamThree = new WidgetParam();
        widgetParamThree.setId(3L);
        widgetParamThree.setName("Name3");
        widgetParamThree.setDescription("Description");
        widgetParamThree.setType(DataTypeEnum.MULTIPLE);
        widgetParamThree.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        WidgetParam widgetParamFour = new WidgetParam();
        widgetParamFour.setId(4L);
        widgetParamFour.setName("Name4");
        widgetParamFour.setDescription("Description");
        widgetParamFour.setType(DataTypeEnum.TEXT);
        widgetParamFour.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        Widget widget = new Widget();
        widget.setId(1L);

        when(categoryService.getCategoryParametersByWidget(any()))
                .thenReturn(Arrays.asList(widgetParam, widgetParamTwo, widgetParamThree, widgetParamFour));

        List<WidgetVariableResponse> actual = widgetService.getWidgetParametersForNashorn(widget);

        assertThat(actual)
                .hasSize(4);

        verify(categoryService, times(1))
                .getCategoryParametersByWidget(widget);
    }

    @Test
    void shouldUpdateWidget() {
        Widget widget = new Widget();
        widget.setId(1L);

        WidgetRequestDto widgetRequestDto = new WidgetRequestDto();
        widgetRequestDto.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);

        when(widgetRepository.findById(any()))
                .thenReturn(Optional.of(widget));
        when(widgetRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        Optional<Widget> actual = widgetService.updateWidget(1L, widgetRequestDto);

        assertThat(actual)
                .isPresent()
                .contains(widget);

        verify(widgetRepository, times(1))
                .findById(1L);
        verify(widgetRepository, times(1))
                .save(widget);
    }

    @Test
    void shouldNotUpdateWidgetWhenNotExist() {
        WidgetRequestDto widgetRequestDto = new WidgetRequestDto();
        widgetRequestDto.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);

        when(widgetRepository.findById(any()))
                .thenReturn(Optional.empty());

        Optional<Widget> actual = widgetService.updateWidget(1L, widgetRequestDto);

        assertThat(actual)
                .isEmpty();

        verify(widgetRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldAddOrUpdateWidgetsNoWidget() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");

        Library library = new Library();
        library.setId(1L);

        Category category = new Category();
        category.setId(1L);

        widgetService.addOrUpdateWidgets(category, Collections.singletonList(library), repository);

        verify(widgetRepository, times(0))
                .findByTechnicalName(any());
        verify(assetService, times(0))
                .save(any());
        verify(widgetParamRepository, times(0))
                .deleteById(any());
        verify(widgetRepository, times(0))
                .save(any());
    }

    @Test
    void shouldAddOrUpdateWidgets() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setContent(new byte[1]);

        Library library = new Library();
        library.setId(1L);
        library.setTechnicalName("technicalName");
        library.setAsset(asset);

        WidgetParamValue currentWidgetParamValue = new WidgetParamValue();
        currentWidgetParamValue.setId(12L);
        currentWidgetParamValue.setJsKey("widgetParamKey");

        WidgetParam currentWidgetParam = new WidgetParam();
        currentWidgetParam.setId(11L);
        currentWidgetParam.setName("widgetParam");
        currentWidgetParam.setPossibleValuesMap(Collections.singletonList(currentWidgetParamValue));

        WidgetParam currentWidgetParamNotPresentAnymore = new WidgetParam();
        currentWidgetParamNotPresentAnymore.setId(13L);

        Asset currentWidgetImage = new Asset();
        currentWidgetImage.setId(10L);

        Widget currentWidget = new Widget();
        currentWidget.setId(1L);
        currentWidget.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);
        currentWidget.setImage(currentWidgetImage);
        currentWidget.setWidgetParams(Arrays.asList(currentWidgetParam, currentWidgetParamNotPresentAnymore));

        Library widgetLibrary = new Library();
        widgetLibrary.setTechnicalName("technicalName");

        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setJsKey("widgetParamKey");

        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setName("widgetParam");
        widgetParam.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        Asset widgetImage = new Asset();

        Widget widget = new Widget();
        widget.setTechnicalName("widgetTechnicalName");
        widget.setLibraries(Collections.singleton(widgetLibrary));
        widget.setImage(widgetImage);
        widget.setWidgetParams(Collections.singletonList(widgetParam));

        Category category = new Category();
        category.setId(1L);
        category.setWidgets(Collections.singleton(widget));

        when(widgetRepository.findByTechnicalName(any()))
                .thenReturn(Optional.of(currentWidget));
        when(assetService.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        widgetService.addOrUpdateWidgets(category, Collections.singletonList(library), repository);

        assertThat(widget.getId()).isEqualTo(1L);
        assertThat(widget.getWidgetAvailability())
                .isEqualTo(WidgetAvailabilityEnum.ACTIVATED);
        assertThat(widget.getCategory())
                .isEqualTo(category);
        assertThat(widget.getRepository())
                .isEqualTo(repository);
        assertThat(widget.getLibraries())
                .contains(library);
        assertThat(widget.getImage().getId())
                .isEqualTo(10L);
        assertThat(new ArrayList<>(widget.getWidgetParams()).get(0).getId())
                .isEqualTo(11L);
        assertThat(new ArrayList<>(new ArrayList<>(widget.getWidgetParams()).get(0).getPossibleValuesMap()).get(0).getId())
                .isEqualTo(12L);

        verify(widgetRepository, times(1))
                .findByTechnicalName("widgetTechnicalName");
        verify(assetService, times(1))
                .save(widgetImage);
        verify(widgetParamRepository, times(1))
                .deleteById(13L);
        verify(widgetRepository, times(1))
                .save(widget);
    }

    @Test
    void shouldAddOrUpdateWidgetsNoAvailability() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");
        
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setContent(new byte[1]);

        Library library = new Library();
        library.setId(1L);
        library.setTechnicalName("technicalName");
        library.setAsset(asset);

        WidgetParamValue currentWidgetParamValue = new WidgetParamValue();
        currentWidgetParamValue.setId(12L);
        currentWidgetParamValue.setJsKey("widgetParamKey");

        WidgetParam currentWidgetParam = new WidgetParam();
        currentWidgetParam.setId(11L);
        currentWidgetParam.setName("widgetParam");
        currentWidgetParam.setPossibleValuesMap(Collections.singletonList(currentWidgetParamValue));

        WidgetParam currentWidgetParamNotPresentAnymore = new WidgetParam();
        currentWidgetParamNotPresentAnymore.setId(13L);

        Asset currentWidgetImage = new Asset();
        currentWidgetImage.setId(10L);

        Widget currentWidget = new Widget();
        currentWidget.setId(1L);
        currentWidget.setImage(currentWidgetImage);
        currentWidget.setWidgetParams(Arrays.asList(currentWidgetParam, currentWidgetParamNotPresentAnymore));

        Library widgetLibrary = new Library();
        widgetLibrary.setTechnicalName("technicalName");

        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setJsKey("widgetParamKey");

        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setName("widgetParam");
        widgetParam.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        Asset widgetImage = new Asset();

        Widget widget = new Widget();
        widget.setTechnicalName("widgetTechnicalName");
        widget.setLibraries(Collections.singleton(widgetLibrary));
        widget.setImage(widgetImage);
        widget.setWidgetParams(Collections.singletonList(widgetParam));

        Category category = new Category();
        category.setId(1L);
        category.setWidgets(Collections.singleton(widget));

        when(widgetRepository.findByTechnicalName(any()))
                .thenReturn(Optional.of(currentWidget));
        when(assetService.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        widgetService.addOrUpdateWidgets(category, Collections.singletonList(library), repository);

        assertThat(widget.getId())
                .isEqualTo(1L);
        assertThat(widget.getWidgetAvailability())
                .isEqualTo(WidgetAvailabilityEnum.ACTIVATED);
        assertThat(widget.getCategory())
                .isEqualTo(category);
        assertThat(widget.getRepository())
                .isEqualTo(repository);
        assertThat(widget.getLibraries())
                .contains(library);
        assertThat(widget.getImage().getId())
                .isEqualTo(10L);
        assertThat(new ArrayList<>(widget.getWidgetParams()).get(0).getId())
                .isEqualTo(11L);
        assertThat(new ArrayList<>(new ArrayList<>(widget.getWidgetParams()).get(0).getPossibleValuesMap()).get(0).getId())
                .isEqualTo(12L);

        verify(widgetRepository, times(1))
                .findByTechnicalName("widgetTechnicalName");
        verify(assetService, times(1))
                .save(widgetImage);
        verify(widgetParamRepository, times(1))
                .deleteById(13L);
        verify(widgetRepository, times(1))
                .save(widget);
    }

    @Test
    void shouldGetWidgetParamValuesAsMap() {
        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        Map<String, String> actual = widgetService.getWidgetParamValuesAsMap(Collections.singleton(widgetParamValue));

        assertThat(actual)
                .containsEntry("key", "value");
    }
}

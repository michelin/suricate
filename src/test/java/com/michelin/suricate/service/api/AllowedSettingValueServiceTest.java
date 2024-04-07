package com.michelin.suricate.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.AllowedSettingValue;
import com.michelin.suricate.repository.AllowedSettingValueRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AllowedSettingValueServiceTest {
    @Mock
    private AllowedSettingValueRepository allowedSettingValueRepository;

    @InjectMocks
    private AllowedSettingValueService allowedSettingValueService;

    @Test
    void shouldFindById() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setTitle("title");
        allowedSettingValue.setValue("value");

        when(allowedSettingValueRepository.findById(1L))
            .thenReturn(Optional.of(allowedSettingValue));

        Optional<AllowedSettingValue> actual = allowedSettingValueService.findById(1L);

        assertThat(actual)
            .isPresent()
            .contains(allowedSettingValue);

        verify(allowedSettingValueRepository)
            .findById(1L);
    }
}

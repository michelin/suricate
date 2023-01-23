package io.suricate.monitoring.services.nashorn.filters;

import io.suricate.monitoring.services.nashorn.script.NashornWidgetScript;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class JavaClassFilterTest {
    private final JavaClassFilter classFilter = new JavaClassFilter();

    @Test
    void testUnauthorizedClass() {
        boolean actual = classFilter.exposeToScripts(File.class.getName());

        assertThat(actual).isFalse();
    }

    @Test
    void testAuthorizedClass() {
        boolean actual = classFilter.exposeToScripts(NashornWidgetScript.class.getName());

        assertThat(actual).isTrue();
    }
}

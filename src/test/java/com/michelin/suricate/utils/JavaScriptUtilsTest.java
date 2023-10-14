package com.michelin.suricate.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.services.js.script.JsEndpoints;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class JavaScriptUtilsTest {
    @Test
    void shouldPrepare() {
        String actual = JavaScriptUtils.prepare("Packages.checkInterrupted()");
        assertThat(actual).isEqualTo("Packages." + JsEndpoints.class.getName() + ".checkInterrupted()");
    }

    @Test
    void shouldInjectInterruptNull() {
        String actual = JavaScriptUtils.injectInterrupt(null);
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldInjectInterruptEmpty() {
        String actual = JavaScriptUtils.injectInterrupt(StringUtils.EMPTY);
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldInjectInterruptNoInject() {
        String actual = JavaScriptUtils.injectInterrupt("var i = {};");
        assertThat(actual).isEqualTo("var i = {};");
    }

    @Test
    void shouldInjectInterruptLoop() {
        String actual = JavaScriptUtils.injectInterrupt("function(){};");
        assertThat(actual).isEqualTo("function(){Packages." + JsEndpoints.class.getName() + ".checkInterrupted();};");

        String actualTwo = JavaScriptUtils.injectInterrupt("function()\n{\nwhile(true)\n{\n}\n};");
        assertThat(actualTwo).isEqualTo(
            "function(){Packages." + JsEndpoints.class.getName() + ".checkInterrupted();\nwhile(true){Packages."
                + JsEndpoints.class.getName() + ".checkInterrupted();\n}\n};");
    }
}

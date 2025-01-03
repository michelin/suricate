package com.michelin.suricate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.michelin.suricate.service.js.script.JsEndpoints;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class JavaScriptUtilsTest {
    @Test
    void shouldPrepare() {
        String actual = JavaScriptUtils.prepare("Packages.checkInterrupted()");
        assertEquals("Packages." + JsEndpoints.class.getName() + ".checkInterrupted()", actual);
    }

    @Test
    void shouldInjectInterruptNull() {
        String actual = JavaScriptUtils.injectInterrupt(null);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldInjectInterruptEmpty() {
        String actual = JavaScriptUtils.injectInterrupt(StringUtils.EMPTY);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldInjectInterruptNoInject() {
        String actual = JavaScriptUtils.injectInterrupt("var i = {};");
        assertEquals("var i = {};", actual);
    }

    @Test
    void shouldInjectInterruptLoop() {
        String actual = JavaScriptUtils.injectInterrupt("function(){};");
        assertEquals("function(){Packages." + JsEndpoints.class.getName() + ".checkInterrupted();};", actual);

        String actualTwo = JavaScriptUtils.injectInterrupt("function()\n{\nwhile(true)\n{\n}\n};");
        assertEquals("function(){Packages." + JsEndpoints.class.getName()
            + ".checkInterrupted();\nwhile(true){Packages."
            + JsEndpoints.class.getName()
            + ".checkInterrupted();\n}\n};", actualTwo);
    }
}

package io.suricate.monitoring.utils;

import io.suricate.monitoring.service.nashorn.script.Methods;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class JavaScriptUtilsTest {

    @Test
    public void testInjectInterruptNull() {
        Assert.assertEquals("", JavaScriptUtils.injectInterrupt(null));
    }

    @Test
    public void testInjectInterruptEmpty() {
        Assert.assertEquals(StringUtils.EMPTY, JavaScriptUtils.injectInterrupt(StringUtils.EMPTY));
    }

    @Test
    public void testInjectInterruptNoInject() {
        Assert.assertEquals("var i = 0;", JavaScriptUtils.injectInterrupt("var i = 0;"));
        Assert.assertEquals("var i = {};", JavaScriptUtils.injectInterrupt("var i = {};"));
    }

    @Test
    public void testInjectInterruptLoop() {
        Assert.assertEquals("function(){Packages." + Methods.class.getName() + ".checkInterrupted();};", JavaScriptUtils.injectInterrupt("function(){};"));
        Assert.assertEquals("function(){Packages." + Methods.class.getName() + ".checkInterrupted();\nwhile(true){Packages." + Methods.class.getName() + ".checkInterrupted();\n}\n};", JavaScriptUtils.injectInterrupt("function()\n{\nwhile(true)\n{\n}\n};"));
    }

    @Test
    public void testprepare() {
        Assert.assertEquals("Packages." + Methods.class.getName() + ".checkInterrupted()", JavaScriptUtils.prepare("Packages.checkInterrupted()"));
    }
}

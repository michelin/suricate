package io.suricate.monitoring.utils;

import io.suricate.monitoring.model.dto.nashorn.WidgetVariableResponse;
import io.suricate.monitoring.model.enums.DataType;
import io.suricate.monitoring.service.nashorn.script.Methods;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class JavascriptUtilsTest {

    @Test
    public void testInjectInterruptNull() {
        Assert.assertEquals("", JavascriptUtils.injectInterrupt(null));
    }

    @Test
    public void testInjectInterruptEmpty() {
        Assert.assertEquals(StringUtils.EMPTY, JavascriptUtils.injectInterrupt(StringUtils.EMPTY));
    }

    @Test
    public void testInjectInterruptNoInject() {
        Assert.assertEquals("var i = 0;", JavascriptUtils.injectInterrupt("var i = 0;"));
        Assert.assertEquals("var i = {};", JavascriptUtils.injectInterrupt("var i = {};"));
    }

    @Test
    public void testInjectInterruptLoop() {
        Assert.assertEquals("function(){Packages." + Methods.class.getName() + ".checkInterrupted();};", JavascriptUtils.injectInterrupt("function(){};"));
        Assert.assertEquals("function(){Packages." + Methods.class.getName() + ".checkInterrupted();\nwhile(true){Packages." + Methods.class.getName() + ".checkInterrupted();\n}\n};", JavascriptUtils.injectInterrupt("function()\n{\nwhile(true)\n{\n}\n};"));
    }

    @Test
    public void testprepare() {
        Assert.assertEquals("Packages." + Methods.class.getName() + ".checkInterrupted()", JavascriptUtils.prepare("Packages.checkInterrupted()"));
    }

    @Test
    public void testNullExtractVariable() {
        Assert.assertTrue(JavascriptUtils.extractVariables(null).isEmpty());
    }

    @Test
    public void testExtractVariable() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_IMG::Choose your image::FILE::\n" +
            "// SURI_STRING:text content:TEXT\n" +
            "// SURI_BOOLEAN");

        Assert.assertEquals(3, list.size());

        Assert.assertEquals("SURI_IMG", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals("FILE", list.get(0).getType().name());
        Assert.assertTrue(list.get(0).isRequired());
        Assert.assertNull(list.get(0).getData());


        Assert.assertEquals("SURI_STRING", list.get(1).getName());
        Assert.assertNull(list.get(1).getDescription());
        Assert.assertNull(list.get(1).getType());
        Assert.assertTrue(list.get(1).isRequired());
        Assert.assertNull(list.get(1).getData());

        Assert.assertEquals("SURI_BOOLEAN", list.get(2).getName());
        Assert.assertNull(list.get(2).getDescription());
        Assert.assertNull(list.get(2).getType());
        Assert.assertTrue(list.get(2).isRequired());
        Assert.assertNull(list.get(2).getData());
    }

    @Test
    public void testExtractVariableOptional() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_IMG::Choose your image::FILE::::OPTIONAL");
        Assert.assertEquals("SURI_IMG", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals("FILE", list.get(0).getType().name());
        Assert.assertNull(list.get(0).getData());
        Assert.assertFalse(list.get(0).isRequired());
    }

    @Test
    public void testExtractVariableRequired() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_IMG::Choose your image::FILE::::REQUIRED");
        Assert.assertEquals("SURI_IMG", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals("FILE", list.get(0).getType().name());
        Assert.assertNull(list.get(0).getData());
        Assert.assertTrue(list.get(0).isRequired());

        list = JavascriptUtils.extractVariables("// SURI_IMG::Choose your image::FILE::::KJKSDSD");
        Assert.assertEquals("SURI_IMG", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals("FILE", list.get(0).getType().name());
        Assert.assertNull(list.get(0).getData());
        Assert.assertTrue(list.get(0).isRequired());
    }

    @Test
    public void testExtractComplexVariable() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_IMG_TEST_1::Choose your image::FILE::::OPTIONAL");
        Assert.assertEquals("SURI_IMG_TEST_1", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals("FILE", list.get(0).getType().name());
        Assert.assertNull(list.get(0).getData());
        Assert.assertFalse(list.get(0).isRequired());
    }

    @Test
    public void testExtractLowerCaseComplexVariable() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_IMG_TEST_1::Choose your image::file::::OPTIONAL");
        Assert.assertEquals("FILE", list.get(0).getType().name());
    }

    @Test
    public void testExtractComplexVariablePlaceHolder() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_IMG_TEST_1::Choose your image::FILE::fixVersion = \"MCP 1.1\" AND project = MTDI::OPTIONAL");
        Assert.assertEquals("SURI_IMG_TEST_1", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals("FILE", list.get(0).getType().name());
        Assert.assertEquals("fixVersion = \"MCP 1.1\" AND project = MTDI", list.get(0).getData());
        Assert.assertFalse(list.get(0).isRequired());
    }

    @Test
    public void testExtractInternalVariable() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_PREVIOUS::Choose your image::BINARY");
        Assert.assertEquals(0, list.size());
    }


    @Test
    public void testExtractMultipleVariable() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("//SURI_TEST \n// SURI_TEST::Choose your image::TEXT::\n SURI_TEST");

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("SURI_TEST", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals(DataType.TEXT, list.get(0).getType());
    }

    @Test
    public void testSecretVariable() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_TEST::Choose your image::PASSWORD::");

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("SURI_TEST", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals(DataType.PASSWORD, list.get(0).getType());
    }

    @Test
    public void testExtractGlobalVariable() {
        List<String> list = JavascriptUtils.extractGlobalVariable("GLOBA DADt T t WIDGET_CONFIG_JIRA_USER, WIDGET_CONFIG_GITLAB_TOKEN");

        assertThat(list).hasSize(2);
        assertThat(list).containsExactly("WIDGET_CONFIG_JIRA_USER", "WIDGET_CONFIG_GITLAB_TOKEN");
    }

    @Test
    public void testExtractGlobalVariableNull() {
        List<String> list = JavascriptUtils.extractGlobalVariable(null);
        assertThat(list).isNull();
        list = JavascriptUtils.extractGlobalVariable("");
        assertThat(list).isNull();
    }

    @Test
    public void testExtractVariableCombo() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_TEST::Choose your image::COMBO::KEY:VALUE,KEY2:VALUE2::OPTIONAL");

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("SURI_TEST", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals(DataType.COMBO, list.get(0).getType());
        assertThat(list.get(0).getValues()).hasSize(2);
        assertThat(list.get(0).getValues().get("KEY")).isEqualTo("VALUE");
        assertThat(list.get(0).getValues().get("KEY2")).isEqualTo("VALUE2");
        assertThat(list.get(0).isRequired()).isFalse();
    }

    @Test
    public void testExtractVariableMultiple() {
        List<WidgetVariableResponse> list = JavascriptUtils.extractVariables("// SURI_TEST::Choose your image::MULTIPLE::KEY:VALUE,KEY2:VALUE2::OPTIONAL");

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("SURI_TEST", list.get(0).getName());
        Assert.assertEquals("Choose your image", list.get(0).getDescription());
        Assert.assertEquals(DataType.MULTIPLE, list.get(0).getType());
        assertThat(list.get(0).getValues()).hasSize(2);
        assertThat(list.get(0).getValues().get("KEY")).isEqualTo("VALUE");
        assertThat(list.get(0).getValues().get("KEY2")).isEqualTo("VALUE2");
        assertThat(list.get(0).isRequired()).isFalse();
    }


    @Test
    public void testParseKeyValueNull() {
        Map<String, String> map = JavascriptUtils.parseKeyValue(null);
        assertThat(map).isNull();
    }

    @Test
    public void testParseKeyValueMalFormed() {
        Map<String, String> map = JavascriptUtils.parseKeyValue("KEYVALUE");
        assertThat(map).isEmpty();
        map = JavascriptUtils.parseKeyValue("KEYVALUE,KEYVALUE2");
        assertThat(map).isEmpty();
        map = JavascriptUtils.parseKeyValue("KEYVALUE,KEYVALUE2,,;;897");
        assertThat(map).isEmpty();
        map = JavascriptUtils.parseKeyValue("KEYVALUE,KEYVALUE2,,;;897,KEY3:VALUE3,,retr");
        assertThat(map).hasSize(1);
        assertThat(map.get("KEY3")).isEqualTo("VALUE3");
    }

    @Test
    public void testParseKeyValue() {
        Map<String, String> map = JavascriptUtils.parseKeyValue("KEY:VALUE,KEY2:VALUE2");
        assertThat(map).hasSize(2);
        assertThat(map.get("KEY")).isEqualTo("VALUE");
        assertThat(map.get("KEY2")).isEqualTo("VALUE2");
        assertThat(map.get("KEY3")).isNull();
    }
}

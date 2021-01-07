package io.suricate.monitoring.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

public class PropertiesUtilsTest {

    @Test
    public void testNullProperties(){
        Assert.assertNull(PropertiesUtils.convertStringWidgetPropertiesToProperties(null));
        Assert.assertEquals(0, PropertiesUtils.convertStringWidgetPropertiesToMap(null).size());
    }

    @Test
    public void testEmptyProperties(){
        Assert.assertNull(PropertiesUtils.convertStringWidgetPropertiesToProperties(""));
        Assert.assertEquals(0,PropertiesUtils.convertStringWidgetPropertiesToMap("").size());
    }

    @Test
    public void testProperties(){
        String props = "key.test=test\nkey2.test=ok";

        Properties properties = PropertiesUtils.convertStringWidgetPropertiesToProperties(props);
        Assert.assertNotNull(properties);
        Assert.assertEquals(2,properties.stringPropertyNames().size());
        Assert.assertEquals("test",properties.getProperty("key.test"));
        Assert.assertEquals("ok",properties.getProperty("key2.test"));

        Map<String,String> map = PropertiesUtils.convertStringWidgetPropertiesToMap(props);
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.entrySet().size());
        Assert.assertEquals("test", map.get("key.test"));
        Assert.assertEquals("ok", map.get("key2.test"));
    }
}

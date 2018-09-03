package io.suricate.monitoring.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class JsonUtilsTest {

    @Test
    public void testJsonValidNull(){
        Assert.assertFalse(JsonUtils.isJsonValid(null));
    }

    @Test
    public void testJsonValidEmpty(){
        Assert.assertFalse(JsonUtils.isJsonValid(StringUtils.EMPTY));
    }

    @Test
    public void testJsonInvalid(){
        Assert.assertFalse(JsonUtils.isJsonValid("{\"test\":0"));
    }

    @Test
    public void testJsonValid(){
        Assert.assertTrue(JsonUtils.isJsonValid("{\"test\":0}"));
    }
}

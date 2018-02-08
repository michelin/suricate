package io.suricate.monitoring.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class JsonUtilsTest {

    @Test
    public void testJsonValidNull(){
        Assert.assertEquals(false, JsonUtils.isJsonValid(null));
    }

    @Test
    public void testJsonValidEmpty(){
        Assert.assertEquals(false, JsonUtils.isJsonValid(StringUtils.EMPTY));
    }

    @Test
    public void testJsonInvalid(){
        Assert.assertEquals(false, JsonUtils.isJsonValid("{\"test\":0"));
    }

    @Test
    public void testJsonValid(){
        Assert.assertEquals(true, JsonUtils.isJsonValid("{\"test\":0}"));
    }
}

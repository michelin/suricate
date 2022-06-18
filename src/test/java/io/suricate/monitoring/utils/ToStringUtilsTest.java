package io.suricate.monitoring.utils;

import org.junit.Assert;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ToStringUtilsTest {

    @Test
    public void testToStringUtilsNull() throws Exception {
        Assert.assertNull(ToStringUtils.toStringEntity(null));
    }

    public static class SimpleEntity {
        int i = 0;
    }

    public static class SimpleEntity2 {
        Long test = 0L;
        String test2 = "ok";
    }

    @Test
    public void testToStringSimpleEntity() throws Exception {
        assertTrue(ToStringUtils.toStringEntity(new SimpleEntity()).contains("[i=0]"));
        assertTrue(ToStringUtils.toStringEntity(new SimpleEntity2()).contains("[test=0,test2=ok]"));
    }

    public static class RootEntity {
        Long test = 0L;
        String test2 = "ok";
        List<DataEntity> list = new ArrayList<>();
        DataEntity data = new DataEntity();
        DataEntity[] tabData = new DataEntity[10];
    }

    @Entity
    public static class DataEntity {
        @Id
        Long id = 52L;
    }

    @Test
    public void testToStringEntity() throws Exception {
        assertTrue(ToStringUtils.toStringEntity(new RootEntity()).contains("[test=0,test2=ok]"));
    }

    @Test
    public void testHideLogNull() throws Exception {
        assertNull(ToStringUtils.hideWidgetConfigurationInLogs(null,null));
        assertNull(ToStringUtils.hideWidgetConfigurationInLogs("",null));
        assertEquals("test", ToStringUtils.hideWidgetConfigurationInLogs("test",null));
        assertNull(ToStringUtils.hideWidgetConfigurationInLogs(null, Collections.emptyList()));
    }

    @Test
    public void testHideLog() throws Exception {
        assertEquals("test ********** ok", ToStringUtils.hideWidgetConfigurationInLogs("test mypassword ok", Collections.singletonList("mypassword")));
    }

}

package io.suricate.monitoring.service.nashorn;

import io.suricate.monitoring.service.nashorn.script.Methods;
import io.suricate.monitoring.utils.ApplicationConstant;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class ClassFilterTest {


    private JavaClassFilter classFilter = new JavaClassFilter();

    @Test
    public void testUnauthorizedClass() throws Exception {
        Assert.assertEquals(false,classFilter.exposeToScripts(File.class.getName()));
        Assert.assertEquals(false,classFilter.exposeToScripts(ApplicationConstant.class.getName()));
    }

    @Test
    public void testAuthorizedClass() throws Exception {
        Assert.assertEquals(true,classFilter.exposeToScripts(Methods.class.getName()));
    }

}

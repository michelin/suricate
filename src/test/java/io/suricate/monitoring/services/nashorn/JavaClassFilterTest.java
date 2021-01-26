package io.suricate.monitoring.services.nashorn;

import io.suricate.monitoring.services.nashorn.filters.JavaClassFilter;
import io.suricate.monitoring.services.nashorn.script.NashornWidgetScript;
import io.suricate.monitoring.utils.ApplicationConstant;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class JavaClassFilterTest {


    private JavaClassFilter classFilter = new JavaClassFilter();

    @Test
    public void testUnauthorizedClass() throws Exception {
        Assert.assertFalse(classFilter.exposeToScripts(File.class.getName()));
        Assert.assertFalse(classFilter.exposeToScripts(ApplicationConstant.class.getName()));
    }

    @Test
    public void testAuthorizedClass() throws Exception {
        Assert.assertTrue(classFilter.exposeToScripts(NashornWidgetScript.class.getName()));
    }

}

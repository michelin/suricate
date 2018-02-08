package io.suricate.monitoring.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class FilesUtilsTest {

    @Test
    public void testFilesUtils() throws Exception {
        List<File> list = FilesUtils.getFolders(new File(FilesUtilsTest.class.getResource("/widgets/").getFile()));
        Assert.assertEquals(5,list.size());
    }

    @Test
    public void testFilesUtilsError() throws Exception {
        Assert.assertNull(FilesUtils.getFolders(null));
    }

    @Test
    public void testGetFiles() throws Exception {
        List<File> list = FilesUtils.getFiles(new File(FilesUtilsTest.class.getResource("/widgets/").getFile()));
        Assert.assertEquals(0,list.size());
        list = FilesUtils.getFiles(new File(FilesUtilsTest.class.getResource("/widgets/jira/").getFile()));
        Assert.assertEquals(2,list.size());
    }

    @Test
    public void testgetFileError() throws Exception {
        Assert.assertNull(FilesUtils.getFiles(null));
    }
}

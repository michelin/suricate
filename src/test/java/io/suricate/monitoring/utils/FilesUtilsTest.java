package io.suricate.monitoring.utils;

import io.suricate.monitoring.model.entities.Asset;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FilesUtilsTest {

    @Test
    public void testFilesUtils() throws Exception {
        List<File> list = FilesUtils.getFolders(new File(FilesUtilsTest.class.getResource("/widgets/").getFile()));
        Assert.assertEquals(5, list.size());
    }

    @Test
    public void testFilesUtilsError() throws Exception {
        assertEquals(0, FilesUtils.getFolders(null).size());
    }

    @Test
    public void testGetFiles() throws Exception {
        List<File> list = FilesUtils.getFiles(new File(FilesUtilsTest.class.getResource("/widgets/").getFile()));
        Assert.assertEquals(0, list.size());
        list = FilesUtils.getFiles(new File(FilesUtilsTest.class.getResource("/widgets/jira/").getFile()));
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testGetFileError() throws Exception {
        assertEquals(0, FilesUtils.getFiles(null).size());
    }

    @Test
    public void testReadAsset() throws Exception {
        Asset asset = FilesUtils.readAsset(new File(FilesUtilsTest.class.getResource("/libraries/d3.min.js").getFile()));
        assertNotNull(asset);
        assertEquals("application/javascript", asset.getContentType());
        assertEquals(3, asset.getSize());
    }

    @Test
    public void testReadImageAsset() throws Exception {
        Asset asset = FilesUtils.readAsset(new File(FilesUtilsTest.class.getResource("/widgets/jira/icon.jpeg").getFile()));
        assertNotNull(asset);
        assertEquals("image/jpeg", asset.getContentType());
        assertEquals(39201, asset.getSize());
    }
}

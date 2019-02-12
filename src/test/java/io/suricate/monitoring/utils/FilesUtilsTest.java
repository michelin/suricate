package io.suricate.monitoring.utils;

import io.suricate.monitoring.model.entity.Asset;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class FilesUtilsTest {

    @Test
    public void testFilesUtils() throws Exception {
        List<File> list = FilesUtils.getFolders(new File(FilesUtilsTest.class.getResource("/widgets/").getFile()));
        Assert.assertEquals(5, list.size());
    }

    @Test
    public void testFilesUtilsError() throws Exception {
        Assert.assertNull(FilesUtils.getFolders(null));
    }

    @Test
    public void testGetFiles() throws Exception {
        List<File> list = FilesUtils.getFiles(new File(FilesUtilsTest.class.getResource("/widgets/").getFile()));
        Assert.assertEquals(0, list.size());
        list = FilesUtils.getFiles(new File(FilesUtilsTest.class.getResource("/widgets/jira/").getFile()));
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testgetFileError() throws Exception {
        Assert.assertNull(FilesUtils.getFiles(null));
    }

    @Test
    public void testReadAsset() throws Exception {
        Asset asset = FilesUtils.readAsset(new File(FilesUtilsTest.class.getResource("/Libraries/d3.min.js").getFile()));
        assertThat(asset).isNotNull();
        assertThat(asset.getContentType()).isEqualTo("application/javascript");
        assertThat(asset.getSize()).isEqualTo(3);
    }

    @Test
    public void testReadImageAsset() throws Exception {
        Asset asset = FilesUtils.readAsset(new File(FilesUtilsTest.class.getResource("/widgets/jira/icon.jpeg").getFile()));
        assertThat(asset).isNotNull();
        assertThat(asset.getContentType()).isEqualTo("image/jpeg");
        assertThat(asset.getSize()).isEqualTo(39201);
    }
}

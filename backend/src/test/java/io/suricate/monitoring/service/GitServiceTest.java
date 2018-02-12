package io.suricate.monitoring.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Ignore
public class GitServiceTest {

    @Autowired
    private GitService gitService;

    @Test(expected = MalformedURLException.class)
    public void testMalformedURL() throws Exception{
        gitService.cloneRepo(null, null);
    }

    @Test
    public void testCloneWidgetRepo() throws Exception{
        File folder = null;
        try {
             folder = gitService.cloneWidgetRepo();
             if (folder.exists()){
                 Collection<File> files = FileUtils.listFilesAndDirs(folder, TrueFileFilter.TRUE, DirectoryFileFilter.DIRECTORY);
                 Assert.assertTrue(files.size() > 3);
             }
        } finally {
            FileUtils.deleteQuietly(folder);
        }
        Assert.assertTrue(!folder.exists());
    }
}

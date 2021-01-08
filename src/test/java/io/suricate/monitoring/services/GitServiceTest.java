package io.suricate.monitoring.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GitServiceTest {

    @Autowired
    private GitService gitService;

    @Test(expected = MalformedURLException.class)
    public void testMalformedURL() throws Exception {
        gitService.cloneRepo(null, null, null, null);
    }
}

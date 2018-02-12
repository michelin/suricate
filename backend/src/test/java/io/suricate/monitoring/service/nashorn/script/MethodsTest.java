package io.suricate.monitoring.service.nashorn.script;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class MethodsTest {

    @Test
    public void testAtobNull(){
        Assert.assertEquals(null, Methods.btoa(null));
    }

    @Test
    public void testAtobEmpty(){
        Assert.assertEquals(null, Methods.btoa(""));
    }

    @Test
    public void testAtob(){
        Assert.assertEquals("c2Rmc2ZkZg==", Methods.btoa("sdfsfdf"));
        Assert.assertEquals("YWZzZGZkZnFzZGYgcXNmIHQnKHQgdHJlenQgYWV0cnpldHpldHJ0", Methods.btoa("afsdfdfqsdf qsf t'(t trezt aetrzetzetrt"));
    }

}

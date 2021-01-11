package io.suricate.monitoring.services.nashorn.script;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class NashornWidgetScriptTest {

    @Test
    public void testAtobNull(){
        Assert.assertNull(NashornWidgetScript.btoa(null));
    }

    @Test
    public void testAtobEmpty(){
        Assert.assertNull(NashornWidgetScript.btoa(""));
    }

    @Test
    public void testAtob(){
        Assert.assertEquals("c2Rmc2ZkZg==", NashornWidgetScript.btoa("sdfsfdf"));
        Assert.assertEquals("YWZzZGZkZnFzZGYgcXNmIHQnKHQgdHJlenQgYWV0cnpldHpldHJ0", NashornWidgetScript.btoa("afsdfdfqsdf qsf t'(t trezt aetrzetzetrt"));
    }

}

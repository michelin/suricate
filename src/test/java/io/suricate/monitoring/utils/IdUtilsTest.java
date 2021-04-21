package io.suricate.monitoring.utils;

import io.suricate.monitoring.utils.exceptions.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class IdUtilsTest {

    @Test
    public void testEncryptNull() throws Exception {
        assertThat(IdUtils.encrypt(null)).isNull();
    }

    @Test(expected = ApiException.class)
    public void testDecryptNull() throws Exception {
        IdUtils.decrypt(null);
    }

    @Test(expected = ApiException.class)
    public void testDecryptBadToken() throws Exception {
        IdUtils.decrypt("dfdfrgregregae");
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        Long id = 12L;
        assertThat(IdUtils.decrypt(IdUtils.encrypt(id))).isEqualTo(12L);
    }

}

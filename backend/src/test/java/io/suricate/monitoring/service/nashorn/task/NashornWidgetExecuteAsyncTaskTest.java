package io.suricate.monitoring.service.nashorn.task;

import io.suricate.monitoring.model.dto.error.FatalError;
import io.suricate.monitoring.model.dto.error.RemoteError;
import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.dto.nashorn.WidgetVariableResponse;
import io.suricate.monitoring.model.enums.NashornErrorTypeEnum;
import io.suricate.monitoring.model.enums.WidgetVariableType;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.Assert;
import org.junit.Test;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class NashornWidgetExecuteAsyncTaskTest {

    @Test
    public void testBadScript() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectId(0L);
        request.setScript("fdsqdfs");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null, widgetVariableResponses);
        NashornResponse response = widgetJob.call();

        Assert.assertTrue(response.isFatal());
        Assert.assertNotNull(response.getLog());
    }

    @Test
    public void testScriptWithoutRunFunction() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectId(0L);
        request.setScript("function test() {}");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null, widgetVariableResponses);
        NashornResponse response = widgetJob.call();

        Assert.assertTrue(response.isFatal());
        Assert.assertNotNull(response.getLog());
    }

    @Test
    public void testBadReturn() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectWidgetId(25L);
        request.setProjectId(10L);
        request.setScript("function run () {}");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null, widgetVariableResponses);
        NashornResponse response = widgetJob.call();


        Assert.assertEquals(NashornErrorTypeEnum.FATAL ,response.getError());
        Assert.assertTrue(response.isFatal());
        Assert.assertNotNull(response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertNull(response.getData());

        // Check with already success widget update
        request.setAlreadySuccess(true);
        response = widgetJob.call();

        Assert.assertEquals(NashornErrorTypeEnum.ERROR , response.getError());
        Assert.assertFalse(response.isFatal());
        Assert.assertNotNull(response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testGoodFunction() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectWidgetId(25L);
        request.setProjectId(10L);
        request.setScript("function run () { return '{}'}");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null, widgetVariableResponses);
        NashornResponse response = widgetJob.call();


        Assert.assertNull(response.getError());
        Assert.assertFalse(response.isFatal());
        Assert.assertNull(response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertEquals("{}",response.getData());
    }

    @Test
    public void testGoodFunctionWithLog() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectWidgetId(25L);
        request.setProjectId(10L);
        request.setScript("function run () { print('ok'); return '{}'}");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null, widgetVariableResponses);
        NashornResponse response = widgetJob.call();


        Assert.assertNull(response.getError());
        Assert.assertFalse(response.isFatal());
        Assert.assertEquals("ok",response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertEquals("{}",response.getData());
    }

    @Test
    public void testUnauthorizedJavaFunctionAccess() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectWidgetId(25L);
        request.setProjectId(10L);
        request.setScript("function run () { var file = Java.type('java.io.File'); file.listRoots(); return '{}'}");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null, widgetVariableResponses);
        NashornResponse response = widgetJob.call();

        Assert.assertNotNull(response.getError());
        Assert.assertTrue(response.isFatal());
        Assert.assertNotNull(response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testJavaFunctionAccess() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectWidgetId(25L);
        request.setProjectId(10L);
        request.setScript("function run () { print(Packages.btoa('test')); return '{}'}");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null, widgetVariableResponses);
        NashornResponse response = widgetJob.call();

        Assert.assertNull(response.getError());
        Assert.assertFalse(response.isFatal());
        Assert.assertEquals("dGVzdA==",response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertEquals("{}",response.getData());
    }

    @Test
    public void testNetworkCall() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectWidgetId(25L);
        request.setProjectId(10L);
        request.setScript("function run () { Packages.call(\"https://localhost/rzer/\", null, null, null); return '{}'}");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null,widgetVariableResponses);
        NashornResponse response = widgetJob.call();

        Assert.assertNotNull(response.getError());
        Assert.assertTrue(response.isFatal());
        Assert.assertNotNull(response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testNetworkCallErrorServer() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectWidgetId(25L);
        request.setProjectId(10L);
        request.setScript("function run () { Packages.throwError(); return '{}'}");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null, widgetVariableResponses);
        NashornResponse response = widgetJob.call();

        Assert.assertNotNull(response.getError());
        Assert.assertFalse(response.isFatal());
        Assert.assertNotNull(response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertNull(response.getData());

        // Check with already success widget update
        request.setAlreadySuccess(true);
        response = widgetJob.call();

        Assert.assertEquals(NashornErrorTypeEnum.ERROR , response.getError());
        Assert.assertFalse(response.isFatal());
        Assert.assertNotNull(response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertNull(response.getData());
    }


    @Test
    public void testTimeOutException() throws Exception {
        NashornRequest request = new NashornRequest();
        request.setDelay(0L);
        request.setPreviousData(null);
        request.setProjectWidgetId(25L);
        request.setProjectId(10L);
        request.setScript("function run () { Packages.throwTimeout(); return '{}'}");

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null, widgetVariableResponses);
        NashornResponse response = widgetJob.call();

        Assert.assertNotNull(response.getError());
        Assert.assertFalse(response.isFatal());
        Assert.assertNotNull(response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertNull(response.getData());

        // Check with already success widget update
        request.setAlreadySuccess(true);
        response = widgetJob.call();

        Assert.assertEquals(NashornErrorTypeEnum.ERROR , response.getError());
        Assert.assertFalse(response.isFatal());
        Assert.assertNotNull(response.getLog());
        Assert.assertEquals(10, (long) response.getProjectId());
        Assert.assertEquals(25, (long) response.getProjectWidgetId());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testEncryptedStringVariable() throws Exception {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("password");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        // Encrypted string
        String encryptedString = "encrypted string *";

        WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
        widgetVariableResponse.setName("SURI_SECRET");
        widgetVariableResponse.setDescription("title");
        widgetVariableResponse.setType(WidgetVariableType.SECRET);
        widgetVariableResponse.setRequired(true);

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        widgetVariableResponses.add(widgetVariableResponse);


        NashornRequest request = new NashornRequest();
        request.setProperties("SURI_SECRET="+encryptor.encrypt(encryptedString));
        request.setScript("function run () { print(SURI_SECRET); return '{}'}");

        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request, encryptor, widgetVariableResponses);
        NashornResponse response = widgetJob.call();

        Assert.assertNull(response.getError());
        Assert.assertFalse(response.isFatal());
        Assert.assertEquals("*******************", response.getLog());
        Assert.assertNotNull(response.getData());
    }

    @Test
    public void testErrorEncryptedStringVariable() throws Exception {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("password");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        // Encrypted string
        String encryptedString = "encrypted string *";

        WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
        widgetVariableResponse.setName("SURI_SECRET");
        widgetVariableResponse.setDescription("title");
        widgetVariableResponse.setType(WidgetVariableType.SECRET);
        widgetVariableResponse.setRequired(true);

        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        widgetVariableResponses.add(widgetVariableResponse);

        NashornRequest request = new NashornRequest();
        request.setProperties("SURI_SECRET=A054578BC");
        request.setScript("function run () { print(SURI_SECRET); return '{}'}");

        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request, encryptor, widgetVariableResponses);
        NashornResponse response = widgetJob.call();

        Assert.assertNotNull(response.getError());
        Assert.assertTrue(response.isFatal());
        Assert.assertNotNull(response.getLog());
    }

    @Test
    public void testIsFatalError() throws Exception {
        NashornRequest nashornRequest = new NashornRequest();
        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();

        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(nashornRequest, null, widgetVariableResponses);
        nashornRequest.setAlreadySuccess(false);

        Assert.assertTrue(widgetJob.isFatalError(new Exception(""), new Exception("")));
        Assert.assertFalse(widgetJob.isFatalError(new Exception("timeoutException"), new Exception("")));
        Assert.assertFalse(widgetJob.isFatalError(new Exception("timeoutException"), new FatalError("")));
        Assert.assertFalse(widgetJob.isFatalError(new Exception("timeout:"), new IllegalArgumentException("")));
        Assert.assertFalse(widgetJob.isFatalError(new Exception("Error on server"), new RemoteError("Error on server")));
        Assert.assertTrue(widgetJob.isFatalError(new Exception("Error on server"), new Exception("Error on server")));

        nashornRequest.setAlreadySuccess(true);
        Assert.assertFalse(widgetJob.isFatalError(new Exception(""), new Exception("")));
        Assert.assertFalse(widgetJob.isFatalError(new Exception("timeoutException"), new Exception("")));
        Assert.assertFalse(widgetJob.isFatalError(new Exception("timeoutException"), new FatalError("")));
        Assert.assertFalse(widgetJob.isFatalError(new Exception("Error on server"), new RemoteError("Error on server")));
        Assert.assertFalse(widgetJob.isFatalError(new Exception("Error on server"), new Exception("Error on server")));
        Assert.assertFalse(widgetJob.isFatalError(new ConnectException("Connection error"), new IllegalArgumentException()));
    }

    @Test
    public void testPrettiFy() throws Exception {
        NashornRequest nashornRequest = new NashornRequest();
        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();
        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(nashornRequest, null, widgetVariableResponses);
        nashornRequest.setAlreadySuccess(false);

        Assert.assertEquals("", widgetJob.prettify("ExecutionException: java.lang.FatalError:"));
        Assert.assertNull(widgetJob.prettify(null));
        Assert.assertEquals("qsfs eart: eztrez tezt ztz: tez r", widgetJob.prettify("qsfs eart: eztrez tezt ztz: tez r"));
    }
}

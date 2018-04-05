package io.suricate.monitoring.service.nashorn.task;

import io.suricate.monitoring.model.dto.error.FatalError;
import io.suricate.monitoring.model.dto.error.RemoteError;
import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.Assert;
import org.junit.Test;

import java.net.ConnectException;

public class NashornWidgetExecuteAsyncTaskTest {

//    @Test
//    public void testBadScript() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectId(0L);
//        request.setScript("fdsqdfs");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//        Assert.assertEquals(response.isFatal(), true);
//        Assert.assertNotNull(response.getLog());
//    }
//
//    @Test
//    public void testScriptWithoutRunFunction() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectId(0L);
//        request.setScript("function test() {}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//        Assert.assertEquals(response.isFatal(), true);
//        Assert.assertNotNull(response.getLog());
//    }
//
//    @Test
//    public void testBadReturn() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectWidgetId(25L);
//        request.setProjectId(10L);
//        request.setScript("function run () {}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//
//        Assert.assertEquals(NashornErrorTypeEnum.FATAL ,response.getError());
//        Assert.assertEquals(response.isFatal(), true);
//        Assert.assertNotNull(response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertNull(response.getData());
//
//        // Check with already success widget update
//        request.setAlreadySuccess(true);
//        response = widgetJob.call();
//
//        Assert.assertEquals(NashornErrorTypeEnum.ERROR , response.getError());
//        Assert.assertEquals(response.isFatal(), false);
//        Assert.assertNotNull(response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertNull(response.getData());
//    }
//
//    @Test
//    public void testGoodFunction() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectWidgetId(25L);
//        request.setProjectId(10L);
//        request.setScript("function run () { return '{}'}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//
//        Assert.assertNull(response.getError());
//        Assert.assertEquals(response.isFatal(), false);
//        Assert.assertNull(response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertEquals("{}",response.getData());
//    }
//
//    @Test
//    public void testGoodFunctionWithLog() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectWidgetId(25L);
//        request.setProjectId(10L);
//        request.setScript("function run () { print('ok'); return '{}'}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//
//        Assert.assertNull(response.getError());
//        Assert.assertEquals(response.isFatal(), false);
//        Assert.assertEquals("ok",response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertEquals("{}",response.getData());
//    }
//
//    @Test
//    public void testUnauthorizedJavaFunctionAccess() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectWidgetId(25L);
//        request.setProjectId(10L);
//        request.setScript("function run () { var file = Java.type('java.io.File'); file.listRoots(); return '{}'}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//        Assert.assertNotNull(response.getError());
//        Assert.assertEquals(response.isFatal(), true);
//        Assert.assertNotNull(response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertNull(response.getData());
//    }
//
//    @Test
//    public void testJavaFunctionAccess() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectWidgetId(25L);
//        request.setProjectId(10L);
//        request.setScript("function run () { print(Packages.btoa('test')); return '{}'}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//        Assert.assertNull(response.getError());
//        Assert.assertEquals(response.isFatal(), false);
//        Assert.assertEquals("dGVzdA==",response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertEquals("{}",response.getData());
//    }
//
//    @Test
//    public void testNetworkCall() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectWidgetId(25L);
//        request.setProjectId(10L);
//        request.setScript("function run () { Packages.call(\"https://localhost/rzer/\", null, null, null); return '{}'}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//        Assert.assertNotNull(response.getError());
//        Assert.assertEquals(response.isFatal(), true);
//        Assert.assertNotNull(response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertNull(response.getData());
//    }
//
//    @Test
//    public void testNetworkCallErrorServer() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectWidgetId(25L);
//        request.setProjectId(10L);
//        request.setScript("function run () { Packages.throwError(); return '{}'}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//        Assert.assertNotNull(response.getError());
//        Assert.assertEquals(response.isFatal(), false);
//        Assert.assertNotNull(response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertNull(response.getData());
//
//        // Check with already success widget update
//        request.setAlreadySuccess(true);
//        response = widgetJob.call();
//
//        Assert.assertEquals(NashornErrorTypeEnum.ERROR , response.getError());
//        Assert.assertEquals(response.isFatal(), false);
//        Assert.assertNotNull(response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertNull(response.getData());
//    }
//
//
//    @Test
//    public void testTimeOutException() throws Exception {
//        NashornRequest request = new NashornRequest();
//        request.setDelay(0L);
//        request.setPreviousData(null);
//        request.setProjectWidgetId(25L);
//        request.setProjectId(10L);
//        request.setScript("function run () { Packages.throwTimeout(); return '{}'}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request,null);
//        NashornResponse response = widgetJob.call();
//
//        Assert.assertNotNull(response.getError());
//        Assert.assertEquals(response.isFatal(), false);
//        Assert.assertNotNull(response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertNull(response.getData());
//
//        // Check with already success widget update
//        request.setAlreadySuccess(true);
//        response = widgetJob.call();
//
//        Assert.assertEquals(NashornErrorTypeEnum.ERROR , response.getError());
//        Assert.assertEquals(response.isFatal(), false);
//        Assert.assertNotNull(response.getLog());
//        Assert.assertEquals(10, (long) response.getProjectId());
//        Assert.assertEquals(25, (long) response.getProjectWidgetId());
//        Assert.assertNull(response.getData());
//    }
//
//    @Test
//    public void testEncryptedStringVariable() throws Exception {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        config.setPassword("password");
//        config.setAlgorithm("PBEWithMD5AndDES");
//        config.setKeyObtentionIterations("1000");
//        config.setPoolSize("1");
//        config.setProviderName("SunJCE");
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        config.setStringOutputType("base64");
//        encryptor.setConfig(config);
//
//        // Encrypted string
//        String encryptedString = "encrypted string *";
//
//        NashornRequest request = new NashornRequest();
//        request.setProperties("SURI_SECRET="+encryptor.encrypt(encryptedString));
//        request.setScript("//SURI_SECRET::title::SECRET::Placeholder\nfunction run () { print(SURI_SECRET); return '{}'}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request, encryptor);
//        NashornResponse response = widgetJob.call();
//
//        Assert.assertNull(response.getError());
//        Assert.assertEquals(response.isFatal(), false);
//        Assert.assertEquals("*******************", response.getLog());
//        Assert.assertNotNull(response.getData());
//    }
//
//    @Test
//    public void testErrorEncryptedStringVariable() throws Exception {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        config.setPassword("password");
//        config.setAlgorithm("PBEWithMD5AndDES");
//        config.setKeyObtentionIterations("1000");
//        config.setPoolSize("1");
//        config.setProviderName("SunJCE");
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        config.setStringOutputType("base64");
//        encryptor.setConfig(config);
//
//        // Encrypted string
//        String encryptedString = "encrypted string *";
//
//        NashornRequest request = new NashornRequest();
//        request.setProperties("SURI_SECRET=A054578BC");
//        request.setScript("//SURI_SECRET::title::SECRET::Placeholder\nfunction run () { print(SURI_SECRET); return '{}'}");
//
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(request, encryptor);
//        NashornResponse response = widgetJob.call();
//
//        Assert.assertNotNull(response.getError());
//        Assert.assertEquals(response.isFatal(), true);
//        Assert.assertNotNull(response.getLog());
//    }
//
//    @Test
//    public void testIsFatalError() throws Exception {
//        NashornRequest nashornRequest = new NashornRequest();
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(nashornRequest, null);
//        nashornRequest.setAlreadySuccess(false);
//
//        Assert.assertEquals(true, widgetJob.isFatalError(new Exception(""),new Exception("")));
//        Assert.assertEquals(false, widgetJob.isFatalError(new Exception("timeoutException"),new Exception("")));
//        Assert.assertEquals(false, widgetJob.isFatalError(new Exception("timeoutException"),new FatalError("")));
//        Assert.assertEquals(false, widgetJob.isFatalError(new Exception("timeout:"),new IllegalArgumentException("")));
//        Assert.assertEquals(false, widgetJob.isFatalError(new Exception("Error on server"),new RemoteError("Error on server")));
//        Assert.assertEquals(true, widgetJob.isFatalError(new Exception("Error on server"),new Exception("Error on server")));
//
//        nashornRequest.setAlreadySuccess(true);
//        Assert.assertEquals(false, widgetJob.isFatalError(new Exception(""),new Exception("")));
//        Assert.assertEquals(false, widgetJob.isFatalError(new Exception("timeoutException"),new Exception("")));
//        Assert.assertEquals(false, widgetJob.isFatalError(new Exception("timeoutException"),new FatalError("")));
//        Assert.assertEquals(false, widgetJob.isFatalError(new Exception("Error on server"),new RemoteError("Error on server")));
//        Assert.assertEquals(false, widgetJob.isFatalError(new Exception("Error on server"),new Exception("Error on server")));
//        Assert.assertEquals(false, widgetJob.isFatalError(new ConnectException("Connection error"),new IllegalArgumentException()));
//    }
//
//    @Test
//    public void testPrettiFy() throws Exception {
//        NashornRequest nashornRequest = new NashornRequest();
//        NashornWidgetExecuteAsyncTask widgetJob = new NashornWidgetExecuteAsyncTask(nashornRequest, null);
//        nashornRequest.setAlreadySuccess(false);
//
//        Assert.assertEquals("", widgetJob.prettify("ExecutionException: java.lang.FatalError:"));
//        Assert.assertEquals(null, widgetJob.prettify(null));
//        Assert.assertEquals("qsfs eart: eztrez tezt ztz: tez r", widgetJob.prettify("qsfs eart: eztrez tezt ztz: tez r"));
//    }
}

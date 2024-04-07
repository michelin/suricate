package com.michelin.suricate.service.js.task;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.js.JsResultDto;
import com.michelin.suricate.model.dto.js.WidgetVariableResponseDto;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.model.enumeration.JsExecutionErrorTypeEnum;
import com.michelin.suricate.util.exception.js.FatalException;
import com.michelin.suricate.util.exception.js.RemoteException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class JsExecutionAsyncTaskTest {
    @ParameterizedTest
    @CsvSource({"badScript,ReferenceError: badScript is not defined",
        "function test() {},No run function defined",
        "function run() {},The JSON response is not valid - null",
        "function run () { var file = Java.type('java.io.File'); file.listRoots(); return '{}'},"
            + "TypeError: Access to host class java.io.File is not allowed or does not exist."})
    void shouldFail(String script, String expectedLogs) {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript(script);

        JsExecutionAsyncTask task = new JsExecutionAsyncTask(jsExecutionDto, null, Collections.emptyList());
        JsResultDto actual = task.call();

        assertThat(actual.isFatal()).isTrue();
        assertThat(actual.getLog()).isEqualTo(expectedLogs);
    }

    @ParameterizedTest
    @CsvSource({"function run() {},The JSON response is not valid - null",
        "function run () { Packages.throwError(); return '{}'},Error",
        "function run () { Packages.throwTimeout(); return '{}'},Timeout"})
    void shouldFailWithErrorBecauseBadReturn(String script, String expectedLogs) {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setAlreadySuccess(true);
        jsExecutionDto.setScript(script);

        JsExecutionAsyncTask task = new JsExecutionAsyncTask(jsExecutionDto, null, Collections.emptyList());
        JsResultDto actual = task.call();

        assertThat(actual.getError()).isEqualTo(JsExecutionErrorTypeEnum.ERROR);
        assertThat(actual.getLog()).isEqualTo(expectedLogs);
    }

    @Test
    void shouldSuccess() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsExecutionAsyncTask task = new JsExecutionAsyncTask(jsExecutionDto, null, Collections.emptyList());
        JsResultDto actual = task.call();

        assertThat(actual.getError()).isNull();
        assertThat(actual.isFatal()).isFalse();
        assertThat(actual.getProjectId()).isEqualTo(1L);
        assertThat(actual.getProjectWidgetId()).isEqualTo(1L);
        assertThat(actual.getData()).isEqualTo("{}");
        assertThat(actual.getLog()).isNull();
    }

    @Test
    void shouldSuccessWithWidgetProperties() {
        WidgetVariableResponseDto widgetParameter = new WidgetVariableResponseDto();
        widgetParameter.setName("SURI_TITLE");
        widgetParameter.setDescription("title");
        widgetParameter.setType(DataTypeEnum.TEXT);
        widgetParameter.setDefaultValue("defaultValue");
        widgetParameter.setRequired(true);

        WidgetVariableResponseDto widgetParameterNotRequired = new WidgetVariableResponseDto();
        widgetParameterNotRequired.setName("NOT_REQUIRED_SURI_TITLE");
        widgetParameterNotRequired.setDescription("not_required_title");
        widgetParameterNotRequired.setType(DataTypeEnum.TEXT);

        List<WidgetVariableResponseDto> widgetParameters = new ArrayList<>();
        widgetParameters.add(widgetParameter);
        widgetParameters.add(widgetParameterNotRequired);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript(
            "function run () { print('title='+SURI_TITLE); "
                + "print('notRequiredTitle='+NOT_REQUIRED_SURI_TITLE); return '{}' }");

        JsExecutionAsyncTask task = new JsExecutionAsyncTask(jsExecutionDto, null, widgetParameters);
        JsResultDto actual = task.call();

        assertThat(actual.getError()).isNull();
        assertThat(actual.isFatal()).isFalse();
        assertThat(actual.getProjectId()).isEqualTo(1L);
        assertThat(actual.getProjectWidgetId()).isEqualTo(1L);
        assertThat(actual.getData()).isEqualTo("{}");
        assertThat(actual.getLog()).contains("title=************");
        assertThat(actual.getLog()).contains("notRequiredTitle=null");
    }

    @Test
    void shouldSuccessWithLogs() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { print('This is a log'); return '{}'; }");

        JsExecutionAsyncTask task = new JsExecutionAsyncTask(jsExecutionDto, null, null);
        JsResultDto actual = task.call();

        assertThat(actual.getError()).isNull();
        assertThat(actual.isFatal()).isFalse();
        assertThat(actual.getProjectId()).isEqualTo(1L);
        assertThat(actual.getProjectWidgetId()).isEqualTo(1L);
        assertThat(actual.getData()).isEqualTo("{}");
        assertThat(actual.getLog()).isEqualTo("This is a log");
    }

    @Test
    void shouldSuccessWithJava() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { print(Packages.btoa('test')); return '{}'}");

        JsExecutionAsyncTask task = new JsExecutionAsyncTask(jsExecutionDto, null, Collections.emptyList());
        JsResultDto actual = task.call();

        assertThat(actual.getError()).isNull();
        assertThat(actual.isFatal()).isFalse();
        assertThat(actual.getProjectId()).isEqualTo(1L);
        assertThat(actual.getProjectWidgetId()).isEqualTo(1L);
        assertThat(actual.getData()).isEqualTo("{}");
        assertThat(actual.getLog()).isEqualTo("dGVzdA==");
    }

    @Test
    void shouldSuccessWithEncryptedVars() {
        WidgetVariableResponseDto widgetParameter = new WidgetVariableResponseDto();
        widgetParameter.setName("SURI_SECRET");
        widgetParameter.setDescription("title");
        widgetParameter.setType(DataTypeEnum.PASSWORD);
        widgetParameter.setRequired(true);

        List<WidgetVariableResponseDto> widgetParameters = new ArrayList<>();
        widgetParameters.add(widgetParameter);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);

        PooledPBEStringEncryptor encryptor = getPooledPbeStringEncryptor();
        jsExecutionDto.setProperties("SURI_SECRET=" + encryptor.encrypt("encrypted string"));
        jsExecutionDto.setScript("function run () { print(SURI_SECRET); return '{}'}");

        JsExecutionAsyncTask task = new JsExecutionAsyncTask(jsExecutionDto, encryptor, widgetParameters);
        JsResultDto actual = task.call();

        assertThat(actual.getError()).isNull();
        assertThat(actual.isFatal()).isFalse();
        assertThat(actual.getProjectId()).isEqualTo(1L);
        assertThat(actual.getProjectWidgetId()).isEqualTo(1L);
        assertThat(actual.getData()).isEqualTo("{}");
        assertThat(actual.getLog()).isEqualTo("****************");
    }

    @Test
    void shouldFailWithEncryptedVars() {
        WidgetVariableResponseDto widgetParameter = new WidgetVariableResponseDto();
        widgetParameter.setName("SURI_SECRET");
        widgetParameter.setDescription("title");
        widgetParameter.setType(DataTypeEnum.PASSWORD);
        widgetParameter.setRequired(true);

        List<WidgetVariableResponseDto> widgetParameters = new ArrayList<>();
        widgetParameters.add(widgetParameter);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setProperties("SURI_SECRET=test");
        jsExecutionDto.setScript("function run () { print(SURI_SECRET); return '{}'}");

        PooledPBEStringEncryptor encryptor = getPooledPbeStringEncryptor();
        JsExecutionAsyncTask task = new JsExecutionAsyncTask(jsExecutionDto, encryptor, widgetParameters);
        JsResultDto actual = task.call();

        assertThat(actual.isFatal()).isTrue();
        assertThat(actual.getLog()).isEqualTo("org.jasypt.exceptions.EncryptionOperationNotPossibleException");
    }

    @Test
    void shouldBeFatalErrorOrNot() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        JsExecutionAsyncTask task = new JsExecutionAsyncTask(jsExecutionDto, null, Collections.emptyList());

        assertThat(task.isFatalError(new Exception(""), new Exception(""))).isTrue();
        assertThat(task.isFatalError(new Exception("Error on server"), new Exception("Error on server"))).isTrue();
        assertThat(task.isFatalError(new Exception("timeoutException"), new Exception(""))).isFalse();
        assertThat(task.isFatalError(new Exception("timeoutException"), new FatalException(""))).isFalse();
        assertThat(task.isFatalError(new Exception("timeout:"), new IllegalArgumentException(""))).isFalse();
        assertThat(
            task.isFatalError(new Exception("Error on server"), new RemoteException("Error on server"))).isFalse();
        assertThat(
            task.isFatalError(new Exception("Error on server"), new UnknownHostException("Error on server"))).isFalse();

        jsExecutionDto.setAlreadySuccess(true);

        assertThat(task.isFatalError(new Exception(""), new Exception(""))).isFalse();
        assertThat(task.isFatalError(new Exception("timeoutException"), new Exception(""))).isFalse();
        assertThat(task.isFatalError(new Exception("timeoutException"), new FatalException(""))).isFalse();
        assertThat(
            task.isFatalError(new Exception("Error on server"), new RemoteException("Error on server"))).isFalse();
        assertThat(task.isFatalError(new Exception("Error on server"), new Exception("Error on server"))).isFalse();
        assertThat(
            task.isFatalError(new ConnectException("Connection error"), new IllegalArgumentException())).isFalse();
    }

    @NotNull
    private static PooledPBEStringEncryptor getPooledPbeStringEncryptor() {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("password");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);
        return encryptor;
    }
}

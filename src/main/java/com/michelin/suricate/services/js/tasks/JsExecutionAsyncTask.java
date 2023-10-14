/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michelin.suricate.services.js.tasks;

import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.js.JsResultDto;
import com.michelin.suricate.model.dto.js.WidgetVariableResponseDto;
import com.michelin.suricate.model.enums.DataTypeEnum;
import com.michelin.suricate.model.enums.JsExecutionErrorTypeEnum;
import com.michelin.suricate.services.js.script.JsEndpoints;
import com.michelin.suricate.utils.JavaScriptUtils;
import com.michelin.suricate.utils.JsonUtils;
import com.michelin.suricate.utils.PropertiesUtils;
import com.michelin.suricate.utils.ToStringUtils;
import com.michelin.suricate.utils.exceptions.js.NoRunFunctionException;
import com.michelin.suricate.utils.exceptions.js.RemoteException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.jasypt.encryption.StringEncryptor;

import java.io.*;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class JsExecutionAsyncTask implements Callable<JsResultDto> {
    private final JsExecutionDto jsExecutionDto;

    private final StringEncryptor stringEncryptor;

    private final List<WidgetVariableResponseDto> widgetParameters;

    /**
     * Method automatically called by the scheduler after the given delay.
     *
     * Convert the widget properties set by the user to a map. Then, decrypt
     * the secret properties and set default value to unset properties.
     *
     * Then, set the mandatory variables to the engine script.
     * - The widget properties
     * - The data of the previous widget execution
     * - The widget instance ID
     *
     * Compile the Javascript script of the widget, evaluate it and get
     * the JSON result
     *
     * The method handles multiple types of exceptions:
     * - InterruptedIOException: triggered when the execution of the widget
     * is interrupted because the Js execution has been canceled (because the
     * user left the dashboard, or because of a timeout, etc...)
     *
     * @return The response from Js result
     */
    @Override
    public JsResultDto call() {
        log.debug("Executing the widget instance {}", jsExecutionDto.getProjectWidgetId());

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setLaunchDate(new Date());

        try (OutputStream output = new ByteArrayOutputStream ()) {
            try (Context context = Context.newBuilder("js")
                    .out(output)
                    .err(output)
                    .allowHostAccess(HostAccess.ALL)
                    .allowHostClassLookup(className -> className.equals(JsEndpoints.class.getName()))
                    .build()) {

                // Get widget parameters values set by the user
                Map<String, String> widgetProperties = PropertiesUtils.convertStringWidgetPropertiesToMap(jsExecutionDto.getProperties());

                // Decrypt widget secret properties
                decryptWidgetProperties(widgetProperties);

                // Set default value to widget properties
                setDefaultValueToWidgetProperties(widgetProperties);

                Value bindings = context.getBindings("js");
                // Populate properties in the engine
                for (Map.Entry<String, String> entry : widgetProperties.entrySet()) {
                    bindings.putMember(entry.getKey().toUpperCase(), entry.getValue());
                }

                // Add the data of the previous execution
                bindings.putMember(JavaScriptUtils.PREVIOUS_DATA_VARIABLE, jsExecutionDto.getPreviousData());

                // Add the project widget id (id of the widget instance)
                bindings.putMember(JavaScriptUtils.WIDGET_INSTANCE_ID_VARIABLE, jsExecutionDto.getProjectWidgetId());

                context.eval("js", JavaScriptUtils.prepare(jsExecutionDto.getScript()));

                Value runFunction = bindings.getMember("run");

                if (runFunction == null) {
                    throw new NoRunFunctionException("No run function defined");
                }

                String json = runFunction.execute().asString();

                if (JsonUtils.isValid(json)) {
                    jsResultDto.setData(json);
                    jsResultDto.setLog(ToStringUtils.hideWidgetConfigurationInLogs(output.toString(), widgetProperties.values()));
                } else {
                    log.debug("The JSON response obtained after the JavaScript execution of the widget instance {} is invalid", jsExecutionDto.getProjectWidgetId());
                    log.debug("The JSON response is: {}", json);

                    jsResultDto.setLog(ToStringUtils.hideWidgetConfigurationInLogs(output + "\nThe JSON response is not valid - " + json, widgetProperties.values()));
                    jsResultDto.setError(jsExecutionDto.isAlreadySuccess() ? JsExecutionErrorTypeEnum.ERROR : JsExecutionErrorTypeEnum.FATAL);
                }
            }
        } catch (Exception exception) {
            Throwable rootCause = ExceptionUtils.getRootCause(exception);

            // Do not set logs during an interruption, as it is caused by a canceling
            // of the Js execution, the return Js result will not be processed by the JsResultAsyncTask
            if (rootCause instanceof InterruptedIOException) {
                log.info("The execution of the widget instance {} has been interrupted", jsExecutionDto.getProjectWidgetId());
            } else {
                log.error("An error has occurred during the JavaScript execution of the widget instance {}", jsExecutionDto.getProjectWidgetId(), exception);

                if (isFatalError(exception, rootCause)) {
                    jsResultDto.setError(JsExecutionErrorTypeEnum.FATAL);
                } else {
                    jsResultDto.setError(JsExecutionErrorTypeEnum.ERROR);
                }

                // If RemoteException/RequestException get custom message, else get root cause
                String logs = ExceptionUtils.getRootCause(exception).getMessage() != null ? ExceptionUtils.getRootCause(exception).getMessage() :
                        ExceptionUtils.getRootCause(exception).toString();
                jsResultDto.setLog(logs);
            }
        } finally {
            jsResultDto.setProjectId(jsExecutionDto.getProjectId());
            jsResultDto.setProjectWidgetId(jsExecutionDto.getProjectWidgetId());
        }

        return jsResultDto;
    }

    /**
     * Decrypt the encrypted widget secret properties
     *
     * @param widgetProperties        The widget properties
     */
    private void decryptWidgetProperties(Map<String, String> widgetProperties) {
        if (widgetParameters != null) {
            for (WidgetVariableResponseDto widgetParameter : widgetParameters) {
                if (widgetParameter.getType() == DataTypeEnum.PASSWORD) {
                    widgetProperties.put(widgetParameter.getName(), stringEncryptor.decrypt(widgetProperties.get(widgetParameter.getName())));
                }
            }
        }
    }

    /**
     * Set the unset variables in the map properties
     *
     * @param widgetProperties        The widget properties
     */
    private void setDefaultValueToWidgetProperties(Map<String, String> widgetProperties) {
        if (widgetParameters != null) {
            for (WidgetVariableResponseDto widgetVariableResponseDto : widgetParameters) {
                if (!widgetProperties.containsKey(widgetVariableResponseDto.getName())) {
                    if (!widgetVariableResponseDto.isRequired()) {
                        widgetProperties.put(widgetVariableResponseDto.getName(), null);
                    } else {
                        widgetProperties.put(widgetVariableResponseDto.getName(), widgetVariableResponseDto.getDefaultValue());
                    }
                }
            }
        }
    }

    /**
     * Check if the returned error is fatal
     *
     * @param e         The exception thrown
     * @param rootCause The root cause of the exception
     * @return true if the error is fatal, false otherwise
     */
    protected boolean isFatalError(Exception e, Throwable rootCause) {
        return !(rootCause instanceof RemoteException
            || StringUtils.containsIgnoreCase(ExceptionUtils.getMessage(e), "timeout")
            || rootCause instanceof UnknownHostException
            || jsExecutionDto.isAlreadySuccess()
        );
    }
}

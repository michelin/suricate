/*
 * Copyright 2012-2018 the original author or authors.
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

package io.suricate.monitoring.service.nashorn.task;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.dto.nashorn.WidgetVariableResponse;
import io.suricate.monitoring.model.dto.nashorn.error.RemoteError;
import io.suricate.monitoring.model.dto.nashorn.error.RequestException;
import io.suricate.monitoring.model.enums.DataType;
import io.suricate.monitoring.model.enums.NashornErrorTypeEnum;
import io.suricate.monitoring.service.nashorn.JavaClassFilter;
import io.suricate.monitoring.utils.JavaScriptUtils;
import io.suricate.monitoring.utils.JsonUtils;
import io.suricate.monitoring.utils.PropertiesUtils;
import io.suricate.monitoring.utils.ToStringUtils;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Class creating a asynchronous task which will execute a Nashorn request updating a widget instance
 */
public class NashornRequestWidgetExecutionAsyncTask implements Callable<NashornResponse> {
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NashornRequestWidgetExecutionAsyncTask.class);

    /**
     * The Nashorn request to execute
     */
    private final NashornRequest nashornRequest;

    /**
     * The string encryptor used to encrypt/decrypt the encrypted/decrypted secret properties
     */
    private final StringEncryptor stringEncryptor;

    /**
     * The list of widget parameters
     */
    private final List<WidgetVariableResponse> widgetParameters;

    /**
     * Constructor
     *
     * @param nashornRequest          The Nashorn request
     * @param stringEncryptor         The string encryptor bean
     * @param widgetParameters The widget parameters
     */
    public NashornRequestWidgetExecutionAsyncTask(NashornRequest nashornRequest,
                                                  StringEncryptor stringEncryptor,
                                                  List<WidgetVariableResponse> widgetParameters) {
        this.nashornRequest = nashornRequest;
        this.stringEncryptor = stringEncryptor;
        this.widgetParameters = widgetParameters;
    }

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
     * @return The response from Nashorn execution
     */
    @Override
    public NashornResponse call() {
        LOGGER.debug("Executing the Nashorn request of the widget instance {}", nashornRequest.getProjectWidgetId());

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setLaunchDate(new Date());

        try {
            NashornScriptEngineFactory nashornScriptEngineFactory = new NashornScriptEngineFactory();

            // Restrict some java class
            ScriptEngine scriptEngine = nashornScriptEngineFactory.getScriptEngine(new JavaClassFilter());

            // Get widget parameters values set by the user
            Map<String, String> widgetProperties = PropertiesUtils.convertStringWidgetPropertiesToMap(nashornRequest.getProperties());

            // Decrypt widget secret properties
            decryptWidgetProperties(widgetProperties, widgetParameters);

            // Set default value to widget properties
            setDefaultValueToWidgetProperties(widgetProperties, widgetParameters);

            // Populate properties in the engine
            for (Map.Entry<String, String> entry : widgetProperties.entrySet()) {
                scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).put(entry.getKey().toUpperCase(), entry.getValue());
            }

            // Add the data of the previous execution
            scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).put(JavaScriptUtils.PREVIOUS_DATA_VARIABLE, nashornRequest.getPreviousData());

            // Add the project widget id (id of the widget instance)
            scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).put(JavaScriptUtils.WIDGET_INSTANCE_ID_VARIABLE, nashornRequest.getProjectWidgetId());

            // Add output buffer
            try (StringWriter sw = new StringWriter()) {
                scriptEngine.getContext().setWriter(sw);

                // Compile the widget Javascript script
                CompiledScript widgetScript = ((Compilable) scriptEngine).compile(JavaScriptUtils.prepare(nashornRequest.getScript()));

                widgetScript.eval();

                // Result of the widget Javascript script
                String json = (String) ((Invocable) scriptEngine).invokeFunction("run");

                if (JsonUtils.isValid(json)) {
                    nashornResponse.setData(json);
                    nashornResponse.setLog(ToStringUtils.hideWidgetConfigurationInLogs(sw.toString(), widgetProperties.values()));
                } else {
                    LOGGER.debug("The JSON response obtained after the execution of the Nashorn request of the widget instance {} is invalid", nashornRequest.getProjectWidgetId());
                    LOGGER.debug("The JSON response is: {}", json);

                    nashornResponse.setLog(ToStringUtils.hideWidgetConfigurationInLogs(sw.toString() + "\nThe JSON response is not valid - " + json, widgetProperties.values()));
                    nashornResponse.setError(nashornRequest.isAlreadySuccess() ? NashornErrorTypeEnum.ERROR : NashornErrorTypeEnum.FATAL);
                }
            }
        } catch (Exception e) {
            LOGGER.error("An error has occurred during the Nashorn request execution of the widget instance: {}", nashornRequest.getProjectWidgetId(), e);

            LOGGER.debug(ExceptionUtils.getMessage(e), e);

            // Check timeout error and remote error
            Throwable rootCause = ExceptionUtils.getRootCause(e);

            if (isFatalError(e, rootCause)) {
                nashornResponse.setError(NashornErrorTypeEnum.FATAL);
            } else {
                nashornResponse.setError(NashornErrorTypeEnum.ERROR);
            }

            if (rootCause instanceof RequestException) {
                nashornResponse.setLog("Service Response:\n\n" + ((RequestException) rootCause).getResponse() + "\n\nTechnical Data:\n\n" + ((RequestException) rootCause).getTechnicalData());
            } else {
                nashornResponse.setLog(prettify(ExceptionUtils.getRootCauseMessage(e)));
            }
        } finally {
            nashornResponse.setProjectId(nashornRequest.getProjectId());
            nashornResponse.setProjectWidgetId(nashornRequest.getProjectWidgetId());
        }

        return nashornResponse;
    }

    /**
     * Decrypt the encrypted widget secret properties
     *
     * @param widgetProperties        The widget properties
     * @param widgetParameters        The list of widget parameters
     */
    private void decryptWidgetProperties(Map<String, String> widgetProperties, List<WidgetVariableResponse> widgetParameters) {
        if (widgetParameters != null) {
            for (WidgetVariableResponse widgetParameter : widgetParameters) {
                if (widgetParameter.getType() == DataType.PASSWORD) {
                    widgetProperties.put(widgetParameter.getName(), stringEncryptor.decrypt(widgetProperties.get(widgetParameter.getName())));
                }
            }
        }
    }

    /**
     * Set the unset variables in the map properties
     *
     * @param widgetProperties        The widget properties
     * @param widgetParameters        The list of widget parameters
     */
    private void setDefaultValueToWidgetProperties(Map<String, String> widgetProperties, List<WidgetVariableResponse> widgetParameters) {
        if (widgetParameters != null) {
            for (WidgetVariableResponse widgetVariableResponse : widgetParameters) {
                if (!widgetProperties.containsKey(widgetVariableResponse.getName())) {
                    if (!widgetVariableResponse.isRequired()) {
                        widgetProperties.put(widgetVariableResponse.getName(), null);
                    } else {
                        widgetProperties.put(widgetVariableResponse.getName(), widgetVariableResponse.getDefaultValue());
                    }
                }
            }
        }
    }

    /**
     * Prettify an error message
     *
     * @param message The message to prettify
     * @return the message without the exception name
     */
    protected String prettify(String message) {
        if (message == null) {
            return null;
        }
        return RegExUtils.replacePattern(message, "ExecutionException: java.lang.FatalError:|FatalError:", "").trim();
    }

    /**
     * Check if the returned error is fatal
     *
     * @param e         The exception thrown
     * @param rootCause The root cause of the exception
     * @return true if the error is fatal, false otherwise
     */
    protected boolean isFatalError(Exception e, Throwable rootCause) {
        return !(rootCause instanceof RemoteError
            || StringUtils.containsIgnoreCase(ExceptionUtils.getMessage(e), "timeout")
            || rootCause instanceof UnknownHostException
            || nashornRequest.isAlreadySuccess()
        );
    }
}

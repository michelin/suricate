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

package io.suricate.monitoring.service.nashorn;

import io.suricate.monitoring.model.dto.error.RemoteError;
import io.suricate.monitoring.model.dto.error.RequestException;
import io.suricate.monitoring.model.dto.nashorn.ErrorType;
import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.dto.widget.WidgetVariableResponse;
import io.suricate.monitoring.model.enums.WidgetVariableType;
import io.suricate.monitoring.utils.JavascriptUtils;
import io.suricate.monitoring.utils.JsonUtils;
import io.suricate.monitoring.utils.PropertiesUtils;
import io.suricate.monitoring.utils.ToStringUtils;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class WidgetJob implements Callable<NashornResponse>{

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetJob.class);

    private final NashornRequest nashornRequest;

    private final StringEncryptor stringEncryptor;

    public WidgetJob(NashornRequest nashornRequest, StringEncryptor stringEncryptor) {
        this.nashornRequest = nashornRequest;
        this.stringEncryptor = stringEncryptor;
    }

    @Override
    public NashornResponse call() throws Exception {
        NashornResponse ret = new NashornResponse();
        ret.setLaunchDate(new Date());
        try {
            NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
            // restrict some java class
            ScriptEngine engine = factory.getScriptEngine(new JavaClassFilter());

            // prepare properties
            Map<String,String> mapProperties = PropertiesUtils.getMap(nashornRequest.getProperties());
            decryptProperties(mapProperties);

            for (Map.Entry<String,String> entry : mapProperties.entrySet()) {
                engine.getBindings(ScriptContext.ENGINE_SCOPE).put(entry.getKey().toUpperCase(), entry.getValue());
            }
            // add param
            engine.getBindings(ScriptContext.ENGINE_SCOPE).put(JavascriptUtils.INTERNAL_PREVIOUS_VARIABLE, nashornRequest.getPreviousData());

            // add instanceid
            engine.getBindings(ScriptContext.ENGINE_SCOPE).put(JavascriptUtils.INSTANCE_ID_VARIABLE, nashornRequest.getProjectWidgetId());

            // add output buffer
            try (StringWriter sw = new StringWriter()) {
                engine.getContext().setWriter(sw);

                // compile script
                CompiledScript scr = ((Compilable) engine).compile(JavascriptUtils.prepare(nashornRequest.getScript()));
                scr.eval();
                Invocable invocable = (Invocable) engine;
                // Result
                String json = (String) invocable.invokeFunction("run");

                if (JsonUtils.isJsonValid(json)) {
                    ret.setData(json);
                    ret.setLog(ToStringUtils.hideConfig(sw.toString(), mapProperties.values()));
                } else {
                    LOGGER.debug("JSON returned not valid - widgetInstance {}", nashornRequest.getProjectWidgetId());
                    LOGGER.debug(json);
                    ret.setLog(ToStringUtils.hideConfig(sw.toString() + "\nReturned json not valid - " + json, mapProperties.values()));
                    ret.setError(nashornRequest.isAlreadySuccess() ? ErrorType.ERROR : ErrorType.FATAL);
                }
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            LOGGER.debug(ExceptionUtils.getMessage(e), e);
            // Check timeout error and remote Error
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (isFatalError(e, rootCause)) {
                ret.setError(ErrorType.FATAL);
            } else {
                ret.setError(ErrorType.ERROR);
            }
            if (rootCause instanceof RequestException) {
                ret.setLog("Service Response:\n\n"+((RequestException) rootCause).getResponse()+"\n\nTechnical Data:\n\n"+((RequestException) rootCause).getTechnicalData());
            } else {
                ret.setLog(prettify(ExceptionUtils.getRootCauseMessage(e)));
            }
        } finally {
            ret.setProjectId(nashornRequest.getProjectId());
            ret.setProjectWidgetId(nashornRequest.getProjectWidgetId());
        }

        return ret;
    }

    /**
     * Method used to decypt properties
     * @param mapProperties the properties map
     */
    private void decryptProperties(Map<String, String> mapProperties) {
        List<WidgetVariableResponse> widgetVariableResponses = JavascriptUtils.extractVariables(nashornRequest.getScript());

        // decrypt encrypted property
        if (widgetVariableResponses != null) {
            for (WidgetVariableResponse widgetVariableResponse : widgetVariableResponses){
                if (WidgetVariableType.SECRET == widgetVariableResponse.getType()){
                    mapProperties.put(widgetVariableResponse.getName(), stringEncryptor.decrypt(mapProperties.get(widgetVariableResponse.getName())));
                } else if (!mapProperties.containsKey(widgetVariableResponse.getName()) && !widgetVariableResponse.isRequired()){ // Set optional as null
                    mapProperties.put(widgetVariableResponse.getName(), null);
                }
            }
        }
    }


    /**
     * Method used to prettify an error message
     * @param message the message to prettify
     * @return the message without the Exception name
     */
    protected String prettify(String message){
        if (message == null){
            return null;
        }
        return StringUtils.replacePattern(message,"ExecutionException: java.lang.FatalError:|FatalError:","").trim();
    }

    /**
     * Method used to check is the return error is fatal
     * @param e Exception throw
     * @param rootCause the root cause exception
     * @return true is the error is fatal false otherwise
     */
    protected boolean isFatalError(Exception e, Throwable rootCause) {
        return !(rootCause instanceof RemoteError
                || StringUtils.containsIgnoreCase(ExceptionUtils.getMessage(e),"timeout")
                || rootCause instanceof UnknownHostException
                || rootCause instanceof ConnectException
                || nashornRequest.isAlreadySuccess()
        );
    }
}

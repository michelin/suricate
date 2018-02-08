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

package io.suricate.monitoring.controllers.backoffice;

import io.suricate.monitoring.config.security.ConnectedUser;
import io.suricate.monitoring.model.AbstractModel;
import io.suricate.monitoring.utils.EntityUtils;
import io.suricate.monitoring.utils.IdUtils;
import io.suricate.monitoring.utils.SecurityUtils;
import org.omnifaces.util.Messages;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ResourceBundle;

public abstract class AbstractManagedBean implements Serializable {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractManagedBean.class.getName());

    /**
     * Method used to get String from resource bundle
     * @param key message key
     * @param param format parameter
     * @return the formatted string
     */
    public String getString(String key, Object ... param) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msg");
        String ret = null;
        if (resourceBundle.containsKey(key)) {
            ret = resourceBundle.getString(key);
            if (param != null) {
                ret = String.format(ret, param);
            }
        } else {
            ret = "?? " + key + " ??";
            LOGGER.error("No message found with key: {}", key);
        }
        return ret;
    }

    /**
     * Method used to add message into faces context
     * @param severity Severity
     * @param key message key
     * @param param message parameter
     */
    protected void addMessage(FacesMessage.Severity severity, String key, Object ... param){
        Messages.addFlashGlobal(new FacesMessage(severity,getString(key,param),""));
        RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
    }

    /**
     * Method used to add message into faces context
     * @param severity Severity
     * @param clientId client id
     * @param key message key
     * @param param message parameter
     */
    protected void addMessageForClient(FacesMessage.Severity severity, String clientId, String key, Object ... param){
        Messages.addFlash(severity, clientId ,getString(key,param));
    }

    /**
     * Method used to redirect the user to the index page
     */
    protected void redirectToIndex(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        try {
            response.sendRedirect("index.xhtml");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }
    }

    /**
     * Method used to get the connected user
     * @return the connected user
     */
    public ConnectedUser getConnectedUser(){
        return SecurityUtils.getConnectedUser();
    }


    /**
     * Method used to encrypt id without salt
     * @param id to encrypt
     * @return the encrypted id
     */
    public static String encrypt(Long id) {
        return IdUtils.encrypt(id);
    }

    /**
     * Method used to encrypt entity id without salt
     * @param entity id to encrypt
     * @return the encrypted id
     */
    public static String encrypt(AbstractModel entity) {
        return IdUtils.encrypt(EntityUtils.getProxiedId(entity));
    }

}

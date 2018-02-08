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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class BackofficeRootController implements ErrorController {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BackofficeRootController.class);

    private static final String ERROR_PATH = "/error";

    /**
     * Error attribute
     */
    @Autowired
    private ErrorAttributes errorAttributes;

    /**
     * Home controller
     * @param request the request object
     * @return the page to display
     */
    @RequestMapping(value = {"/", "/content"})
    public String gotoHomePage(HttpServletRequest request) {
        return "redirect:"+request.getContextPath()+"/content/index.xhtml";
    }

    /**
     * Endpoint to handle tv path
     * @return the page to display
     */
    @RequestMapping(value = {"/tv"})
    public String gotoTvHome() {
        return "redirect:content/tv/index.xhtml";
    }


    /**
     * Endpoint to handle error page path
     * @param request the request
     * @return the location of the error page
     */
    @RequestMapping(value = ERROR_PATH)
    public String gotoErrorPage(HttpServletRequest request) {
        LOGGER.error(getErrorAttributes(request,true).toString());
        return "redirect:/error.xhtml";
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * Method used to extract attribute from request
     * @param request
     * @param includeStackTrace
     * @return
     */
    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }
}

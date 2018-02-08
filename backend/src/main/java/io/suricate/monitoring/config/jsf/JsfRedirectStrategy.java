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

package io.suricate.monitoring.config.jsf;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.DefaultRedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsfRedirectStrategy extends DefaultRedirectStrategy {

    private static final String PARTIAL_RESPONSE_PREFIX = "<partial-response><redirect url=\"";
    private static final String PARTIAL_RESPONSE_SUFFIX = "\"/></partial-response>";

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {

        if (isAjaxRequest(request)) {

            try {
                String responseUrl = url;
                // in order for the url to be valid in the XML content
                if (StringUtils.contains(responseUrl, "&")) {
                    responseUrl = StringUtils.replace(responseUrl, "&", "&amp;");
                }

                returnPartialResponseRedirect(request, response, responseUrl);
                return;

            } catch (Exception e) {
                logger.error("[sendRedirect: failed]", e);
            }
        }

        super.sendRedirect(request, response, url);
    }

    /**
     * Copied from org.springframework.faces.webflow.JsfAjaxHandler.isAjaxRequestInternal
     * @param request
     * @return
     */
    private static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("Faces-Request");
        String param = request.getParameter("javax.faces.partial.ajax");
        return "partial/ajax".equals(header) || "true".equals(param);
    }

    private void returnPartialResponseRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);
        request.getSession().setAttribute("errorDetail","app.error.timeout");

        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        String responseStr = PARTIAL_RESPONSE_PREFIX + redirectUrl + PARTIAL_RESPONSE_SUFFIX;

        PrintWriter out = response.getWriter();
        out.write(responseStr); // NOSONAR
        out.flush();
        out.close();
    }

}

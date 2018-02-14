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

package io.suricate.monitoring.controllers.api.error;

import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.dto.error.CustomError;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice("io.suricate.monitoring.controllers.api")
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ResponseEntity<?> handleApiException(HttpServletRequest request, ApiException ex) {
        return ResponseEntity.status(ex.getError().getStatus()).body(ex.getError());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> handleRequestException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomError(extractMessage(ex), "request.parameter.error" , HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * Method used to extract message from MethodArgumentNotValidException exception
     * @param ex exception used to extract error fields
     * @return an error string
     */
    private static String extractMessage(MethodArgumentNotValidException ex){
        StringBuilder builder = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if (builder.length()>0){
                builder.append(", ");
            }
            builder.append(error.getField()).append(' ').append(error.getDefaultMessage());
        }
        return builder.toString();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> handleException(HttpServletRequest request, Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomError(ex.getMessage(), "server.error" , HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

}

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

package io.suricate.monitoring.utils.jsf.converter;

import io.suricate.monitoring.model.dto.user.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("userDtoConverter")
@Component
public class UserDtoConverter implements Converter {


    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(StringUtils.isNotBlank(value)) {
            return new UserDto(value);
        }
        return null;
    }

    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((UserDto) object).getUsername());
        }
        return null;
    }
}
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

package io.suricate.monitoring.config.security.token;

import io.suricate.monitoring.controllers.api.exception.ApiException;
import io.suricate.monitoring.model.Configuration;
import io.suricate.monitoring.model.dto.error.ApiError;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.ConfigurationRepository;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.utils.ApplicationConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    private StringEncryptor stringEncryptor;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;

    /**
     * Method used to get user from token
     * @param token the parameter token
     * @return the user
     */
    public User extractToken(String token){
        User ret = null;
        try {
            String val = stringEncryptor.decrypt(token);
            // Check token expiration
            String time = StringUtils.substringAfter(val,";");
            if (StringUtils.isNotBlank(time) && new Date().getTime() > Long.parseLong(time)){
                throw new ApiException(ApiError.TOKEN_EXPIRED);
            }
            ret = userRepository.findByToken(token);
        } catch (ApiException ae) {
            throw ae;
        } catch(Exception e){
            LOGGER.trace(e.getMessage(), e);
            throw new ApiException(ApiError.TOKEN_INVALID);
        }

        return ret;
    }

    /**
     * Method used to generate Token
     * @return the generated token
     */
    public String generateToken(){
        String ret = null;
        try {
            Configuration configuration = configurationRepository.findOne(ApplicationConstant.TOKEN_DELAY);
            if (configuration == null || configuration.getValue() == null) {
                ret = stringEncryptor.encrypt(UUID.randomUUID().toString());
            } else {
                Date date = DateUtils.addMinutes(new Date(), Integer.parseInt(configuration.getValue()));
                ret = stringEncryptor.encrypt(UUID.randomUUID().toString()+";"+date.getTime());
            }
        } catch(Exception e){
            throw new RuntimeException("Error during token generation: "+e.getMessage(),e);
        }

        return ret;
    }

}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.service.token;

import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.property.ApplicationProperties;
import com.michelin.suricate.security.LocalUser;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * JWT helper service.
 */
@Slf4j
@Service
public class JwtHelperService {
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Build a JWT token.
     *
     * @param authentication The authentication information
     * @return The JWT token
     */
    public String createToken(Authentication authentication) {
        LocalUser userPrincipal = (LocalUser) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("firstname", userPrincipal.getUser().getFirstname());
        claims.put("lastname", userPrincipal.getUser().getLastname());
        claims.put("email", userPrincipal.getUser().getEmail());
        claims.put("avatar_url", userPrincipal.getUser().getAvatarUrl());
        claims.put("roles",
            userPrincipal.getUser().getRoles().stream().map(Role::getName).toList());
        claims.put("mode", userPrincipal.getUser().getAuthenticationMethod());

        Date now = new Date();
        Date expiryDate =
            new Date(now.getTime() + applicationProperties.getAuthentication().getJwt().getTokenValidityMs());

        return Jwts.builder()
            .subject(userPrincipal.getUsername())
            .expiration(expiryDate)
            .issuedAt(now)
            .claims(claims)
            .signWith(Keys.hmacShaKeyFor(applicationProperties.getAuthentication().getJwt().getSigningKey().getBytes()))
            .compact();
    }

    /**
     * Extract the username from the given token.
     *
     * @param token The token
     * @return The username
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(
                Keys.hmacShaKeyFor(applicationProperties.getAuthentication().getJwt().getSigningKey().getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    /**
     * Validate a given JWT token.
     *
     * @param authToken The token to validate
     * @return true if it is valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(
                    Keys.hmacShaKeyFor(applicationProperties.getAuthentication().getJwt().getSigningKey().getBytes()))
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
        } catch (Exception e) {
            log.error("Error reading JWT token", e);
        }
        return false;
    }
}

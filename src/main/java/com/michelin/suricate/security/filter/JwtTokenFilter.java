package com.michelin.suricate.security.filter;

import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.ApiErrorEnum;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.UserService;
import com.michelin.suricate.services.token.JwtHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtHelperService jwtHelperService;

    @Autowired
    private UserService userService;

    /**
     * When a request arrives to the Back-End, check if it contains a bearer token.
     * If it does, validate it and set authentication to Spring context
     * @param request The incoming request
     * @param response The response
     * @param filterChain The filter chain
     * @throws IOException Any IO exception
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            final String token = getJwtTokenFromRequest(request);

            if (StringUtils.hasText(token) && jwtHelperService.validateToken(token)) {
                String username = jwtHelperService.getUsernameFromToken(token);
                User user = userService.getOneByUsername(username).orElseThrow(() -> new BadCredentialsException("Bad credentials"));
                LocalUser localUser = new LocalUser(user, Collections.emptyMap());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(localUser, null, localUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
            response.sendError(HttpStatus.UNAUTHORIZED.value(), ApiErrorEnum.AUTHENTICATION_ERROR.getMessage());
        }
    }

    /**
     * Extract the token from a given request, if exists
     * @param request The request
     * @return The token
     */
    private String getJwtTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}

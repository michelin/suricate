package io.suricate.monitoring.security.filter;

import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.services.token.JwtHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtTokenFilter extends OncePerRequestFilter {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    /**
     * The jwt service
     */
    @Autowired
    private JwtHelperService jwtHelperService;

    /**
     * The user service
     */
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
                User user = userService.getOneByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + " is not authorized"));
                LocalUser localUser = new LocalUser(user, Collections.emptyMap());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(localUser, null, localUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            LOGGER.error("Could not set user authentication in security context", e);
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

package com.michelin.suricate.security.filter;

import com.michelin.suricate.model.entities.PersonalAccessToken;
import com.michelin.suricate.model.enums.ApiErrorEnum;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.PersonalAccessTokenService;
import com.michelin.suricate.services.token.PersonalAccessTokenHelperService;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to handle personal access token authentication.
 */
@Slf4j
public class PersonalAccessTokenFilter extends OncePerRequestFilter {
    @Autowired
    private PersonalAccessTokenHelperService patHelperService;

    @Autowired
    private PersonalAccessTokenService patService;

    /**
     * When a request arrives to the Back-End, check if it contains a personal access token.
     * If it does, validate it and set authentication to Spring context
     *
     * @param request     The incoming request
     * @param response    The response
     * @param filterChain The filter chain
     * @throws IOException Any IO exception
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
        throws IOException {
        try {
            final String token = getPersonalAccessTokenFromRequest(request);

            if (StringUtils.hasText(token) && patHelperService.validateToken(token)) {
                Optional<PersonalAccessToken> patOptional =
                    patService.findByChecksum(patHelperService.computePersonAccessTokenChecksum(token));
                if (patOptional.isPresent()) {
                    LocalUser localUser = new LocalUser(patOptional.get().getUser(), Collections.emptyMap());
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(localUser, null, localUser.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
            response.sendError(HttpStatus.UNAUTHORIZED.value(), ApiErrorEnum.AUTHENTICATION_ERROR.getMessage());
        }
    }

    /**
     * Extract the token from a given request, if exists.
     *
     * @param request The request
     * @return The token
     */
    private String getPersonalAccessTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Token ")) {
            return bearerToken.substring(6);
        }

        return null;
    }
}

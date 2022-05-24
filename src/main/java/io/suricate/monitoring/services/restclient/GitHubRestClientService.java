package io.suricate.monitoring.services.restclient;

import io.suricate.monitoring.model.dto.restclient.GitHubUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "github", url = "https://api.github.com/")
public interface GitHubRestClientService {
    /**
     * Get the user information from GitHub token
     * @param token The token
     * @return The GitHub user info
     */
    @GetMapping(value = "/user")
    GitHubUserDto getUser(@RequestHeader(value = "Authorization") String token);
}

package io.suricate.monitoring.model.dto.user;

import io.suricate.monitoring.model.dto.AbstractDto;

public class CredentialsDto extends AbstractDto {
    private String login;
    private String password;
    private boolean rememberMe;

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

}

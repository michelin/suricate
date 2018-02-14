package io.suricate.monitoring.model.dto.token;

public class JWTTokenDto {
    private String token;

    /**
     * Constructor using field
     * @param token the token
     */
    public JWTTokenDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

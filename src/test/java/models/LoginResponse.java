package models;

public class LoginResponse {

    private String access_token;
    private String token_type;
    private Integer expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public Integer getExpires_in() {
        return expires_in;
    }
}

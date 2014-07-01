package br.com.uwant.models.classes;

public class User extends Person {

    private static final User INSTANCE = new User();

    private String token;

    public static User getInstance() {
        return INSTANCE;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

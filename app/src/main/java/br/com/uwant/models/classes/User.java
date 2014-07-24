package br.com.uwant.models.classes;

import java.io.Serializable;

public class User extends Person implements Serializable {

    private static final User INSTANCE = new User();
    public static final String EXTRA = "extra_user";

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

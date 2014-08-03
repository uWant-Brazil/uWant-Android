package br.com.uwant.models.classes;

import java.io.Serializable;

public class User extends Person implements Serializable {

    public static final String EXTRA = "extra_user";
    public static final String EXTRA_ADD_CONTACTS = "extra_add_contacts";
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

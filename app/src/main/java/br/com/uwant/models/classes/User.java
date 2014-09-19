package br.com.uwant.models.classes;

import java.io.Serializable;

public class User extends Person implements Serializable {

    public static final String EXTRA = "extra_user";
    public static final String EXTRA_ADD_CONTACTS = "extra_add_contacts";
    private static final User INSTANCE = new User();

    private String token;
    private String facebookToken;

    public static User getInstance() {
        return INSTANCE;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public static void newInstance(User user) {
        User instance = getInstance();
        instance.setToken(instance.getToken() == null ? user.getToken() : instance.getToken());
        instance.setLogin(user.getLogin());
        instance.setName(user.getName());
        instance.setGender(user.getGender());
        instance.setBirthday(user.getBirthday());
        instance.setFacebookToken(user.getFacebookToken());
        instance.setPicture(user.getPicture());
        instance.setMail(user.getMail());
        instance.setId(user.getId());
    }
}

package br.com.uwant.models.classes;

import java.io.Serializable;

public class User extends Person implements Serializable {

    /**
     * Constante para análise durante a serialização dessa classe.
     */
    public static final String EXTRA = "extra_user";

    /**
     * Constante para análise durante a serialização dessa classe.
     */
    public static final String EXTRA_ADD_CONTACTS = "extra_add_contacts";

    /**
     * Singleton.
     */
    private static final User INSTANCE = new User();

    /**
     * Token responsável pela autenticação no WS.
     */
    private String token;

    /**
     * Token do facebook.
     */
    private String facebookToken;

    public static User getInstance() {
        return INSTANCE;
    }

    public static void newInstance(User user) {
        INSTANCE.setId(user.getId());
        INSTANCE.setLogin(user.getLogin());
        INSTANCE.setMail(user.getMail());
        INSTANCE.setName(user.getName());
        INSTANCE.setGender(user.getGender());
        INSTANCE.setPicture(user.getPicture());
        INSTANCE.setToken(INSTANCE.getToken() == null ? user.getToken() : INSTANCE.getToken());
        INSTANCE.setBirthday(user.getBirthday());
        INSTANCE.setFacebookToken(user.getFacebookToken());
        INSTANCE.setFriendshipLevel(user.getFriendshipLevel());
    }

    public static void clearInstance() {
        INSTANCE.setId(-1);
        INSTANCE.setLogin(null);
        INSTANCE.setMail(null);
        INSTANCE.setName(null);
        INSTANCE.setGender(null);
        INSTANCE.setPicture(null);
        INSTANCE.setToken(null);
        INSTANCE.setBirthday(null);
        INSTANCE.setFacebookToken(null);
        INSTANCE.setFriendshipLevel(null);
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

}

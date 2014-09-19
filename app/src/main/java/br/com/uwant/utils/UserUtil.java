package br.com.uwant.utils;

import br.com.uwant.models.classes.User;

public abstract class UserUtil {

    public static boolean hasFacebook() {
        User user = User.getInstance();
        String token = user.getFacebookToken();
        return (token != null && !token.isEmpty());
    }

}

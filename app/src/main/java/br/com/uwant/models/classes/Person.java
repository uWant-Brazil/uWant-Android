package br.com.uwant.models.classes;

import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Person implements Serializable {

    public static final String EXTRA = "extra_person";

    public enum Gender {
        FEMALE, MALE;
    }

    private String name;
    private String login;
    private String mail;
    private Date birthday;
    private Gender gender;
    private Multimedia picture;

    public Person() {
    }

    public Person(GraphUser friend) {
        final String name = friend.getName();
        final String mail = (String) friend.getProperty("email");
        GraphObject go = (GraphObject) friend.getProperty("picture");
        if (go != null) {
            String url = (String) go.getProperty("url");
            if (url != null && !url.isEmpty()) {
                Multimedia picture = new Multimedia();
                picture.setUrl(url);
                this.picture = picture;
            }
        }


        this.name = name;
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Multimedia getPicture() {
        return picture;
    }

    public void setPicture(Multimedia picture) {
        this.picture = picture;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}

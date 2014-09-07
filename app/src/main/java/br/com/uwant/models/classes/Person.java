package br.com.uwant.models.classes;

import com.facebook.model.GraphUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Person implements Serializable {

    public static final String EXTRA = "extra_person";

    public enum Gender {
        FEMALE, MALE;
    }

    private long id;
    private String name;
    private String login;
    private String mail;
    private Date birthday;
    private Gender gender;
    private Multimedia picture;

    public Person() {
    }

    public Person(long id, String login, String name) {
        this.id = id;
        this.login = login;
        this.name = name;
    }

    public Person(GraphUser friend) {
        final String name = ((String) friend.getProperty("first_name")) + " " + ((String) friend.getProperty("last_name"));
        final String mail = (String) friend.getProperty("email");
        JSONObject go = (JSONObject) friend.getProperty("picture");

        if (go != null) {
            JSONObject picture = null;
            try {
                picture = go.getJSONObject("data");
                if (picture != null && picture.has("url")) {
                    Multimedia pictureM = new Multimedia();
                    String url = picture.getString("url");
                    pictureM.setUrl(url);
                    this.picture = pictureM;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        this.name = name;
        this.mail = mail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

package br.com.uwant.models.classes;

import java.util.Date;

public class Person {

    public enum Gender {
        FEMALE, MALE;
    }

    private String name;
    private String login;
    private Date birthday;
    private Gender gender;
    private Multimedia picture;

    public Person() {
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
}

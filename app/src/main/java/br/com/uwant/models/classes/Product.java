package br.com.uwant.models.classes;

import java.io.Serializable;
import java.util.Comparator;

public class Product implements Serializable{

    private long id = 0;
    private long wishListId;
    private String name;
    private String nickName;
    private Manufacturer manufacturer;
    private Multimedia picture;
    /*
    * Attibuto utilizado para criacao do adapater
    */
    private boolean fake;

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

    public Multimedia getPicture() {
        return picture;
    }

    public void setPicture(Multimedia picture) {
        this.picture = picture;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public boolean isFake() {
        return fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public long getWishListId() {
        return wishListId;
    }

    public void setWishListId(long wishListId) {
        this.wishListId = wishListId;
    }
}

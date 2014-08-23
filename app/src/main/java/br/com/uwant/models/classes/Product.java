package br.com.uwant.models.classes;

import java.io.Serializable;

public class Product implements Serializable {

    private Multimedia picture;

    public Multimedia getPicture() {
        return picture;
    }

    public void setPicture(Multimedia picture) {
        this.picture = picture;
    }
}

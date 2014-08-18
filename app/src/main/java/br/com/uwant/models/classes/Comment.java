package br.com.uwant.models.classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by felipebenezi on 01/07/14.
 */
public class Comment implements Serializable {

    private long id;
    private String text;
    private Person who;
    private Date since;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Person getWho() {
        return who;
    }

    public void setWho(Person who) {
        this.who = who;
    }
}

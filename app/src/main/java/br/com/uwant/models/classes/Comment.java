package br.com.uwant.models.classes;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe de modelagem para comentários realizados por usuário em ações.
 */
public class Comment implements Serializable {

    /**
     * Identificador único.
     */
    private long id;

    /**
     * Mensagem do comentário.
     */
    private String text;

    /**
     * Usuário responsável por comentar.
     */
    private Person who;

    /**
     * Quando esse comentário aconteceu.
     */
    private Date since;

    private boolean uWant;
    private int UWantsCount;

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

    public boolean isuWant() {
        return uWant;
    }

    public void setuWant(boolean uWant) {
        this.uWant = uWant;
    }

    public int getUWantsCount() {
        return UWantsCount;
    }

    public void setUWantsCount(int UWantsCount) {
        this.UWantsCount = UWantsCount;
    }
}

package br.com.uwant.models.classes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Classe de modelagem para "ações" realizadas pela o usuário que são persistidas no WS.
 * Elas possuem vários tipos, por isso, cada tipo tem sua peculiaridade de resposta.
 */
public class Action implements Serializable {

    public enum Type {
        ADD_FRIENDS_CIRCLE,
        ACCEPT_FRIENDS_CIRCLE,
        COMMENT,
        MENTION,
        SHARE,
        WANT,
        REPORT,
        MESSAGE,
        ACTIVITY; // Feed!
    }

    /**
     * Identificador único.
     */
    private long id;

    /**
     * Flag para informar se o usuário "wantou" a ação.
     */
    private boolean uWant;

    /**
     * Flag para informar se o usuário compartilhou a ação.
     */
    private boolean uShare;

    /**
     * Mensagem da ação.
     */
    private String message;

    /**
     * Mensagem extra da ação.
     */
    private String extra;

    /**
     * Tipo da ação.
     */
    private Type type;

    /**
     * Usuário responsável pela ação.
     */
    private Person from;

    /**
     * Quando ocorreu essa ação.
     */
    private Date when;

    /**
     * Lista de comentários da ação.
     */
    private List<Comment> comments;

    /**
     * Contador de "wants" da ação.
     */
    private int UWantsCount;

    /**
     * Contador de comentários da ação.
     */
    private int commentsCount;

    /**
     * Quantidade de compartilhamentos da ação.
     */
    private int SharesCount;
    private WishList wishList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Person getFrom() {
        return from;
    }

    public void setFrom(Person from) {
        this.from = from;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getUWantsCount() {
        return UWantsCount;
    }

    public void setUWantsCount(int UWantsCount) {
        this.UWantsCount = UWantsCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getSharesCount() {
        return SharesCount;
    }

    public void setSharesCount(int sharesCount) {
        SharesCount = sharesCount;
    }

    public boolean isuWant() {
        return uWant;
    }

    public void setuWant(boolean uWant) {
        this.uWant = uWant;
    }

    public boolean isuShare() {
        return uShare;
    }

    public void setuShare(boolean uShare) {
        this.uShare = uShare;
    }

    public WishList getWishList() {
        return wishList;
    }

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
    }
}

package br.com.uwant.models.classes;

import java.util.Date;
import java.util.List;

/**
 * Created by felipebenezi on 01/07/14.
 */
public class Action {

    public enum Type {
        ADD_FRIENDS_CIRCLE,
        ACCEPT_FRIENDS_CIRCLE,
        COMMENT,
        MENTION,
        SHARE,
        WANT,
        REPORT,
        MESSAGE;
    }

    private String message;
    private String extra;
    private Type type;
    private Person from;
    private Date when;
    private List<Comment> comments;
    private int UWantsCount;
    private int commentsCount;
    private int SharesCount;

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
}

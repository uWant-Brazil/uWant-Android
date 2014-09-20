package br.com.uwant.models.classes;

import java.io.Serializable;

public class Multimedia implements Serializable {

    private String url;
    private Object uri;
    private Object bitmap;

    public Multimedia() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getUri() {
        return uri;
    }

    public void setUri(Object uri) {
        this.uri = uri;
    }

    public void setBitmap(Object bitmap) {
        this.bitmap = bitmap;
    }

    public Object getBitmap() {
        return bitmap;
    }
}

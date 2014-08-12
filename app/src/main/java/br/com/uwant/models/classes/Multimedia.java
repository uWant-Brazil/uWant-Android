package br.com.uwant.models.classes;

import android.net.Uri;

import java.io.Serializable;

public class Multimedia implements Serializable {

    private String url;
    private Uri uri;

    public Multimedia() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}

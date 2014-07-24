package br.com.uwant.models.classes;

import android.net.Uri;

public class Multimedia {

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

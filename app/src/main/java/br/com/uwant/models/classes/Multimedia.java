package br.com.uwant.models.classes;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Classe de modelagem para arquivos multimídias (i.e. apenas FOTOS) enviados pela plataforma.
 * Além disso, essa classe também guarda referências locais (i.e. sdcard, memória, etc)
 * desses mesmos arquivos a fim de melhorarmos a performance na renderização.
 */
public class Multimedia implements Serializable {

    /**
     * URL da foto.
     */
    private String url;

    /**
     * Caminho local da foto.
     */
    private Uri uri;

    /**
     * Foto carregada em memória.
     */
    private Bitmap bitmap;

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

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}

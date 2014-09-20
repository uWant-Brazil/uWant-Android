package br.com.uwant.models.cloud.helpers;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class UWFileBody extends FileBody {

    private UWFileBodyListener mListener;
    private UWOutputStreamProgress mOutstream;

    public UWFileBody(File file, UWFileBodyListener listener) {
        super(file);
        this.mListener = listener;
    }

    public UWFileBody(File file, ContentType contentType, UWFileBodyListener listener) {
        super(file, contentType);
        this.mListener = listener;
    }

    public UWFileBody(File file, ContentType contentType, String filename, UWFileBodyListener listener) {
        super(file, contentType, filename);
        this.mListener = listener;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        this.mOutstream = new UWOutputStreamProgress(out, this.mListener, (int) getFile().length());
        super.writeTo(this.mOutstream);
    }

}

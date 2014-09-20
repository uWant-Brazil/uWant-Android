package br.com.uwant.models.cloud.helpers;

import java.io.IOException;
import java.io.OutputStream;

public class UWOutputStreamProgress extends OutputStream {

    private final OutputStream mOutstream;
    private final UWFileBodyListener mListener;
    private int mBytesWritten = 0;
    private int mTotalBytes;

    public UWOutputStreamProgress(OutputStream outstream, UWFileBodyListener listener, int length) {
        this.mOutstream = outstream;
        this.mListener = listener;
        this.mTotalBytes = length;
        listener.preWrite(length);
    }

    @Override
    public void write(int b) throws IOException {
        mOutstream.write(b);
        mBytesWritten += b;
        mListener.written(mTotalBytes, mBytesWritten);
    }

    @Override
    public void write(byte[] b) throws IOException {
        mOutstream.write(b);
        mBytesWritten += b.length;
        mListener.written(mTotalBytes, mBytesWritten);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        mOutstream.write(b, off, len);
        mBytesWritten += len;
        mListener.written(mTotalBytes, mBytesWritten);
    }

    @Override
    public void flush() throws IOException {
    mOutstream.flush();
    }

    @Override
    public void close() throws IOException {
        mOutstream.close();
    }

}

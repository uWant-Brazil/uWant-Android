package br.com.uwant.flow.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import br.com.uwant.R;

/**
 * Fragment padrão para Dialog com ProgressBar para processo assíncrono.
 */
public class ProgressFragmentDialog extends DialogFragment {

    /**
     * Tag de identificação do Fragment.
     */
    private static final String TAG = "ProgressTag";
    private String mMessage;
    private int mMessageId = R.string.text_wait;

    public static ProgressFragmentDialog create(int message) {
        ProgressFragmentDialog fd = new ProgressFragmentDialog();
        fd.setMessage(message);
        return fd;
    }

    public static ProgressFragmentDialog create(String message) {
        ProgressFragmentDialog fd = new ProgressFragmentDialog();
        fd.setMessage(message);
        return fd;
    }

    public static ProgressFragmentDialog show(int message, FragmentManager manager) {
        ProgressFragmentDialog pfd = create(message);
        pfd.show(manager, TAG);
        return pfd;
    }

    public static ProgressFragmentDialog show(String message, FragmentManager manager) {
        ProgressFragmentDialog pfd = create(message);
        pfd.show(manager, TAG);
        return pfd;
    }

    public static ProgressFragmentDialog show(FragmentManager manager) {
        ProgressFragmentDialog pfd = new ProgressFragmentDialog();
        pfd.show(manager, TAG);
        return pfd;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = mMessage == null ? getString(mMessageId) : mMessage;
        return ProgressDialog.show(getActivity(), getString(R.string.app_name), message);
    }

    private void setMessage(String message) {
        this.mMessage = message;
    }

    private void setMessage(int messageId) {
        this.mMessageId = messageId;
    }

}

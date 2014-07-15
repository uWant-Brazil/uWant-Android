package br.com.uwant.flow.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import br.com.uwant.R;

public class ProgressFragmentDialog extends DialogFragment {

    private static final String TAG = "ProgressTag";
    private String message = "Aguarde...";

    public static ProgressFragmentDialog create(String message) {
        ProgressFragmentDialog fd = new ProgressFragmentDialog();
        fd.setMessage(message);
        return fd;
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
        return ProgressDialog.show(getActivity(), getString(R.string.app_name), this.message);
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package br.com.uwant.flow.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.uwant.R;

public class AlertFragmentDialog extends DialogFragment {

    private static final int DEFAULT_VIEW_ID = R.layout.dialog_default;

    private String mTitle;
    private String mMessage;

    private View mContentView;
    private View mCustomView;
    private View mContentPanel;
    private FrameLayout mCustomPanel;
    private TextView mTitleView;
    private TextView mMessageView;
    private DialogInterface.OnClickListener mListenerOk;
    private DialogInterface.OnClickListener mListenerCancel;

    public static AlertFragmentDialog create(String title, String message) {
        return create(title, message, null, null);
    }

    public static AlertFragmentDialog create(String title, View customView) {
        return create(title, customView, null, null);
    }

    public static AlertFragmentDialog create(String title, String message, DialogInterface.OnClickListener listener) {
        return create(title, message, listener, null);
    }

    public static AlertFragmentDialog create(String title, View customView, DialogInterface.OnClickListener listener) {
        return create(title, customView, listener, null);
    }

    public static AlertFragmentDialog create(String title, View customView, DialogInterface.OnClickListener listenerOk, DialogInterface.OnClickListener listenerCancel) {
        AlertFragmentDialog fd = new AlertFragmentDialog();
        fd.setTitle(title);
        fd.setListenerOk(listenerOk);
        fd.setListenerCancel(listenerCancel);
        fd.setCustomView(customView);
        return fd;
    }

    public static AlertFragmentDialog create(String title, String message, DialogInterface.OnClickListener listenerOk, DialogInterface.OnClickListener listenerCancel) {
        AlertFragmentDialog fd = new AlertFragmentDialog();
        fd.setTitle(title);
        fd.setMessage(message);
        fd.setListenerOk(listenerOk);
        fd.setListenerCancel(listenerCancel);
        return fd;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = LayoutInflater.from(getActivity()).inflate(DEFAULT_VIEW_ID, null);

            mContentPanel = mContentView.findViewById(R.id.contentPanel);
            mCustomPanel = (FrameLayout) mContentView.findViewById(R.id.customPanel);
            mTitleView = (TextView) mContentView.findViewById(R.id.alertTitle);
            mMessageView = (TextView) mContentView.findViewById(R.id.message);

            mTitleView.setTextColor(getResources().getColor(R.color.BLACK));

            final ImageView imageViewIcon = (ImageView) mContentView.findViewById(R.id.icon);
            imageViewIcon.setVisibility(View.GONE);
        }

        mTitleView.setText(mTitle);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mContentView);

        if (mCustomView == null) {
            mContentPanel.setVisibility(View.VISIBLE);
            mCustomPanel.setVisibility(View.GONE);

            mMessageView.setText(mMessage);
        } else {
            mContentPanel.setVisibility(View.GONE);
            mCustomPanel.setVisibility(View.VISIBLE);

            mCustomPanel.addView(mCustomView);
        }

        builder.setPositiveButton("Ok", mListenerOk);
        if (mListenerOk != null) {
            if (mListenerCancel != null) {
                builder.setNegativeButton("Cancelar", mListenerCancel);
            }
        }

        return builder.create();
    }

    private void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    private void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    private void setListenerOk(DialogInterface.OnClickListener mListenerOk) {
        this.mListenerOk = mListenerOk;
    }

    private void setListenerCancel(DialogInterface.OnClickListener mListenerCancel) {
        this.mListenerCancel = mListenerCancel;
    }

    private void setCustomView(View customView) {
        this.mCustomView = customView;
    }
}

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

/**
 * Fragment responsável pela exibição do AlertDialog com o theme padrão do App.
 */
public class AlertFragmentDialog extends DialogFragment {

    /**
     * Resource padrão para os AlertDialog's do App.
     */
    private static final int DEFAULT_VIEW_ID = R.layout.dialog_default;

    /**
     * Título do AlertDialog.
     */
    private String mTitle;

    /**
     * Mensagem do AlertDialog.
     */
    private String mMessage;

    private String mPositiveText;
    private String mNegativeText;

    private View mContentView;
    private View mCustomView;
    private View mContentPanel;
    private FrameLayout mCustomPanel;
    private TextView mTitleView;
    private TextView mMessageView;
    private DialogInterface.OnClickListener mListenerOk;
    private DialogInterface.OnClickListener mListenerCancel;

    public static AlertFragmentDialog create(String title, String message, String positiveText) {
        return create(title, message, positiveText, null, null, null);
    }

    public static AlertFragmentDialog create(String title, String message) {
        return create(title, message, null, null, null, null);
    }

    public static AlertFragmentDialog create(String title, View customView, String positiveText) {
        return create(title, customView, positiveText, null, null, null);
    }

    public static AlertFragmentDialog create(String title, View customView) {
        return create(title, customView, null, null, null, null);
    }

    public static AlertFragmentDialog create(String title, String message, String positiveText, DialogInterface.OnClickListener listener) {
        return create(title, message, positiveText, listener, null, null);
    }

    public static AlertFragmentDialog create(String title, String message, DialogInterface.OnClickListener listener) {
        return create(title, message, null, listener, null, null);
    }

    public static AlertFragmentDialog create(String title, View customView, String positiveText, DialogInterface.OnClickListener listener) {
        return create(title, customView, positiveText, listener, null, null);
    }

    public static AlertFragmentDialog create(String title, View customView, DialogInterface.OnClickListener listener) {
        return create(title, customView, null, listener, null, null);
    }

    public static AlertFragmentDialog create(String title, View customView, DialogInterface.OnClickListener listenerOk, DialogInterface.OnClickListener listenerCancel) {
        return create(title, customView, null, listenerOk, null, listenerCancel);
    }

    public static AlertFragmentDialog create(String title, View customView, String positiveText, DialogInterface.OnClickListener listenerOk, String negativeText, DialogInterface.OnClickListener listenerCancel) {
        AlertFragmentDialog fd = new AlertFragmentDialog();
        fd.setTitle(title);
        fd.setListenerOk(listenerOk);
        fd.setListenerCancel(listenerCancel);
        fd.setCustomView(customView);
        fd.setPositiveText(positiveText);
        fd.setNegativeText(negativeText);
        return fd;
    }

    public static AlertFragmentDialog create(String title, String message, DialogInterface.OnClickListener listenerOk, DialogInterface.OnClickListener listenerCancel) {
        return create(title, message, null, listenerOk, null, listenerCancel);
    }

    public static AlertFragmentDialog create(String title, String message, String positiveText, DialogInterface.OnClickListener listenerOk, String negativeText, DialogInterface.OnClickListener listenerCancel) {
        AlertFragmentDialog fd = new AlertFragmentDialog();
        fd.setTitle(title);
        fd.setMessage(message);
        fd.setListenerOk(listenerOk);
        fd.setListenerCancel(listenerCancel);
        fd.setPositiveText(positiveText);
        fd.setNegativeText(negativeText);
        return fd;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setContentView();

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

        if (mPositiveText == null) {
            mPositiveText = getString(R.string.text_ok);
        }

        builder.setPositiveButton(mPositiveText, mListenerOk);
        if (mListenerOk != null) {
            if (mNegativeText == null) {
                mNegativeText = getString(R.string.text_cancel);
            }

            builder.setNegativeButton(mNegativeText, mListenerCancel);
        }

        return builder.create();
    }

    private void setContentView() {
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
    }

    private void setTitle(String title) {
        this.mTitle = title;
    }

    private void setMessage(String message) {
        this.mMessage = message;
    }

    private void setListenerOk(DialogInterface.OnClickListener listener) {
        this.mListenerOk = listener;
    }

    private void setListenerCancel(DialogInterface.OnClickListener listener) {
        this.mListenerCancel = listener;
    }

    private void setCustomView(View customView) {
        this.mCustomView = customView;
    }

    public void setPositiveText(String positiveText) {
        this.mPositiveText = positiveText;
    }

    public void setNegativeText(String negativeText) {
        this.mNegativeText = negativeText;
    }
}

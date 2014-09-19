package br.com.uwant.flow.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.uwant.R;

/**
 * Fragment padrão para Dialog com ProgressBar para processo assíncrono.
 */
public class ProgressFragmentDialog extends DialogFragment {

    /**
     * Tag de identificação do Fragment.
     */
    private static final String TAG = "ProgressTag";

    /**
     * Resource padrão para os AlertDialog's do App.
     */
    private static final int DEFAULT_VIEW_ID = R.layout.dialog_default;

    private View mContentView;
    private View mCustomView;
    private FrameLayout mCustomPanel;

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
        setContentView();

        String message = mMessage == null ? getString(mMessageId) : mMessage;
        TextView textViewMessage = (TextView) mCustomView.findViewById(R.id.progress_textView_message);
        textViewMessage.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mContentView);
        builder.setCancelable(false);

        return builder.create();
    }

    private void setContentView() {
        if (mContentView == null) {
            mContentView = LayoutInflater.from(getActivity()).inflate(DEFAULT_VIEW_ID, null);

            View mContentPanel = mContentView.findViewById(R.id.contentPanel);
            mContentPanel.setVisibility(View.GONE);

            mCustomPanel = (FrameLayout) mContentView.findViewById(R.id.customPanel);
            mCustomPanel.setVisibility(View.VISIBLE);

            mCustomView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_progress_content_default, mCustomPanel, false);
            mCustomPanel.addView(mCustomView);

            TextView mTitleView = (TextView) mContentView.findViewById(R.id.alertTitle);
            mTitleView.setTextColor(getResources().getColor(R.color.BLACK));
            mTitleView.setText(R.string.app_name);

            final ImageView imageViewIcon = (ImageView) mContentView.findViewById(R.id.icon);
            imageViewIcon.setVisibility(View.GONE);
        }
    }

    private void setMessage(String message) {
        this.mMessage = message;
    }

    private void setMessage(int messageId) {
        this.mMessageId = messageId;
    }

}

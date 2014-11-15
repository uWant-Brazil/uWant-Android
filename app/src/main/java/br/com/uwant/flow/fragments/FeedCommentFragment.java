package br.com.uwant.flow.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.MainActivity;
import br.com.uwant.models.adapters.FeedCommentsAdapter;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Comment;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.CommentModel;
import br.com.uwant.models.cloud.models.ListCommentsModel;
import br.com.uwant.models.cloud.models.WantModel;
import br.com.uwant.utils.KeyboardUtil;

public class FeedCommentFragment extends Fragment implements View.OnClickListener,
        IRequest.OnRequestListener<Action>, AdapterView.OnItemClickListener {

    public static final String TAG = "feed_comment";

    private Action mAction;
    private List<Comment> mComments;
    private FeedCommentsAdapter mAdapter;

    private ListView mListView;
    private EditText mEditTextComment;
    private boolean isPrimeiraVez;

    private final IRequest.OnRequestListener<Action> LISTENER_WANT = new IRequest.OnRequestListener<Action>() {

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onExecute(Action action) {
        }

        @Override
        public void onError(RequestError error) {
            // TODO ... Caso de erro? Voltar? Rollback?
        }

    };

    public static FeedCommentFragment newInstance(Action action) {
        FeedCommentFragment f = new FeedCommentFragment();
        f.setAction(action);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPrimeiraVez = true;
        this.mComments = new ArrayList<Comment>(25);
        this.mAdapter = new FeedCommentsAdapter(getActivity(), this.mComments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.feed_comment_listView);
        mListView.setAdapter(this.mAdapter);
        mListView.setEmptyView(view.findViewById(R.id.contacts_gridView_loading));
        mListView.setOnItemClickListener(this);

        mEditTextComment = (EditText) view.findViewById(R.id.feed_comment_editText);

        final LinearLayout linearLayoutTop = (LinearLayout) view.findViewById(R.id.feed_comment_linearLayout_top);
        linearLayoutTop.setOnClickListener(this);

        final ImageButton imageButtonSend = (ImageButton) view.findViewById(R.id.feed_comment_imageButton_send);
        imageButtonSend.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.fadeIn();
        }

        refresh();
    }

    private void refresh() {
        ListCommentsModel model = new ListCommentsModel();
        model.setAction(this.mAction);
        Requester.executeAsync(model, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.fadeOut();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feed_comment_imageButton_send:
                send();
                break;

            case R.id.feed_comment_linearLayout_top:
                toggleProgress();
                break;

            default:
                break;
        }
    }

    private void toggleProgress() {
        if (isPrimeiraVez) {
            isPrimeiraVez = false;
            return;
        }

        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.feed_comment_progress_top);
        ImageView imageView = (ImageView) getView().findViewById(R.id.feed_comment_imageView_top);

        int visibility = progressBar.isShown() ? View.GONE : View.VISIBLE;
        int visibility2 = imageView.isShown() ? View.GONE : View.VISIBLE;
        progressBar.setVisibility(visibility);
        imageView.setVisibility(visibility2);
    }

    public void setAction(Action action) {
        this.mAction = action;
    }

    private void send() {
        String comment = mEditTextComment.getText().toString();
        if (comment != null && !comment.isEmpty()) {
            KeyboardUtil.hide(mEditTextComment);

            CommentModel model = new CommentModel();
            model.setAction(this.mAction);
            model.setComment(comment);

            Requester.executeAsync(model, this);
            mEditTextComment.setText("");
        } else {
            Toast.makeText(getActivity(), R.string.text_alert_comment_empty, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute(Action action) {
        if (this.mComments != null && this.mAdapter != null) {
            if (this.mComments.size() > 0) {
                toggleProgress();
            }

            this.mComments.clear();

            LinearLayout linearLayoutTop = (LinearLayout) getView().findViewById(R.id.feed_comment_linearLayout_top);
            List<Comment> comments = action.getComments();
            if (comments != null && comments.size() > 0) {
                linearLayoutTop.setVisibility(View.VISIBLE);
                this.mComments.addAll(comments);
            } else {
                linearLayoutTop.setVisibility(View.GONE);
                getView().findViewById(R.id.contacts_gridView_loading).setVisibility(View.GONE);
                mListView.setEmptyView(getView().findViewById(R.id.feed_comment_linearLayout_empty));
            }

            this.mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
        toggleProgress();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Comment comment = this.mComments.get(position);
        if (comment != null) {
            WantModel wantModel = new WantModel();
            wantModel.setComment(comment);

            Requester.executeAsync(wantModel, LISTENER_WANT);

            toggleWant(comment);
        }
    }

    private void toggleWant(Comment comment) {
        boolean isWanted = comment.isuWant();
        int count = comment.getUWantsCount();
        if (!isWanted || count > 0) { // Equivalente a [!isWanted || (count > 0 && isWanted)]
            comment.setUWantsCount(isWanted ? --count : ++count);
            comment.setuWant(!isWanted);
            mAdapter.notifyDataSetChanged();
        }
    }

}

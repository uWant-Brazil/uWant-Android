package br.com.uwant.flow.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
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

/**
 * Created by felipebenezi on 18/08/14.
 */
public class FeedCommentFragment extends Fragment implements View.OnClickListener, IRequest.OnRequestListener<Action> {

    public static final String TAG = "feed_comment";

    private Action mAction;
    private List<Comment> mComments;
    private FeedCommentsAdapter mAdapter;

    private ListView mListView;
    private EditText mEditTextComment;

    public static FeedCommentFragment newInstance(Action action) {
        FeedCommentFragment f = new FeedCommentFragment();
        f.setAction(action);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mEditTextComment = (EditText) view.findViewById(R.id.feed_comment_editText);

        ImageButton imageButtonSend = (ImageButton) view.findViewById(R.id.feed_comment_imageButton_send);
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

    public void updateContent(Action action) {
        if (this.mComments != null && this.mAdapter != null) {
            this.mComments.clear();

            List<Comment> comments = action.getComments();
            if (comments != null && comments.size() > 0) {
                this.mComments.addAll(comments);
            } else {
                getView().findViewById(R.id.contacts_gridView_loading).setVisibility(View.GONE);
                mListView.setEmptyView(null);
            }

            this.mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feed_comment_imageButton_send:
                send();
                break;

            default:
                break;
        }
    }

    public void setAction(Action action) {
        this.mAction = action;
    }

    private void send() {
        String comment = mEditTextComment.getText().toString();
        if (comment != null && !comment.isEmpty()) {
            CommentModel model = new CommentModel();
            model.setAction(this.mAction);
            model.setComment(comment);

            Requester.executeAsync(model, this);
        }
    }

    @Override
    public void onPreExecute() {
        mEditTextComment.setText("");
    }

    @Override
    public void onExecute(Action result) {
        this.mAction.setComments(result.getComments());
        this.mComments.clear();
        this.mComments.addAll(result.getComments());
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
    }
}

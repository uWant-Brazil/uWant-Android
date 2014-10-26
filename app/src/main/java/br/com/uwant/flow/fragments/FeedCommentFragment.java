package br.com.uwant.flow.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.MainActivity;
import br.com.uwant.models.adapters.FeedCommentsAdapter;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Comment;

/**
 * Created by felipebenezi on 18/08/14.
 */
public class FeedCommentFragment extends Fragment {

    public static final String TAG = "feed_comment";

    private List<Comment> mComments;
    private FeedCommentsAdapter mAdapter;

    private ListView mListView;

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
}

package br.com.uwant.flow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
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
        final ListView listView = (ListView) view.findViewById(R.id.feed_comment_listView);
        listView.setAdapter(this.mAdapter);
        listView.setEmptyView(view.findViewById(R.id.contacts_gridView_loading));
    }

    public void updateContent(Action action) {
        this.mComments.clear();

        List<Comment> comments = action.getComments();
        if (comments != null && comments.size() > 0) {
            this.mComments.addAll(comments);
        }

        this.mAdapter.notifyDataSetChanged();
    }
}

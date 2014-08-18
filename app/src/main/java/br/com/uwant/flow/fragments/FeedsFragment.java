package br.com.uwant.flow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.adapters.FeedsAdapter;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Comment;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.FeedsModel;
import br.com.uwant.models.cloud.models.ListCommentsModel;
import br.com.uwant.models.cloud.models.ShareModel;
import br.com.uwant.models.cloud.models.WantModel;

public class FeedsFragment extends Fragment implements View.OnClickListener,
        IRequest.OnRequestListener<List<Action>> {

    public static final String TAG = "feedsFragment";
    private static final int DEFAULT_START_INDEX = 0;
    private static final int DEFAULT_END_INDEX = 20;
    private static final FeedsModel MODEL = new FeedsModel();

    private List<Action> mActions;
    private FeedsAdapter mFeedsAdapter;
    private GridView mGridView;

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

    private final IRequest.OnRequestListener<Action> LISTENER_SHARE = new IRequest.OnRequestListener<Action>() {

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

    private final IRequest.OnRequestListener<Action> LISTENER_COMMENTS = new IRequest.OnRequestListener<Action>() {

        public FeedCommentFragment mFragment;

        @Override
        public void onPreExecute() {
            mFragment = new FeedCommentFragment();
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top, R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom)
                    .replace(android.R.id.content, mFragment, FeedCommentFragment.TAG)
                    .addToBackStack(FeedCommentFragment.TAG)
                    .commit();
        }

        @Override
        public void onExecute(Action action) {
            mFragment.updateContent(action);
        }

        @Override
        public void onError(RequestError error) {
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActions = new ArrayList<Action>(25);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feeds, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFeedsAdapter = new FeedsAdapter(getActivity(), mActions, this);

        mGridView = (GridView) view.findViewById(R.id.main_gridView);
        mGridView.setNumColumns(1);
        mGridView.setAdapter(mFeedsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActions.clear();
        mFeedsAdapter.notifyDataSetChanged();

        MODEL.setStartIndex(DEFAULT_START_INDEX);
        MODEL.setEndIndex(DEFAULT_END_INDEX);

        Requester.executeAsync(MODEL, this);
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute(List<Action> result) {
        if (result != null) {
            mActions.addAll(result);
            mFeedsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        final Action action = mFeedsAdapter.getItem(position);;

        switch (view.getId()) {
            case R.id.adapter_feeds_button_uwants:
                WantModel wantModel = new WantModel();
                wantModel.setAction(action);

                Requester.executeAsync(wantModel, LISTENER_WANT);
                toggleWant(action);
                break;

            case R.id.adapter_feeds_button_comments:
                listComments(action);
                break;

            case R.id.adapter_feeds_button_shares:
                ShareModel shareModel = new ShareModel();
                shareModel.setAction(action);

                Requester.executeAsync(shareModel, LISTENER_WANT);
                toggleShare(action);
                break;

            default:
                break;
        }
    }

    private void listComments(Action action) {
        ListCommentsModel model = new ListCommentsModel();
        model.setAction(action);

        Requester.executeAsync(model, LISTENER_COMMENTS);
    }

    private void toggleShare(Action action) {
        boolean isShared = action.isuShare();
        if (!isShared) {
            int count = action.getSharesCount();
            if (count > 0) {
                action.setUWantsCount(++count);
                mFeedsAdapter.notifyDataSetChanged();
            }
        }
    }

    private void toggleWant(Action action) {
        boolean isWanted = action.isuWant();
        int count = action.getUWantsCount();
        if (count > 0) {
            action.setUWantsCount(isWanted ? --count : ++count);
            action.setuWant(!isWanted);
            mFeedsAdapter.notifyDataSetChanged();
        }
    }
}

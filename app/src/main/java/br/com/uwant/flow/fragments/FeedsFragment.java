package br.com.uwant.flow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.adapters.FeedsAdapter;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.ActionReportModelAbstract;
import br.com.uwant.models.cloud.models.BlockFriendModelAbstract;
import br.com.uwant.models.cloud.models.ExcludeFriendModelAbstract;
import br.com.uwant.models.cloud.models.FeedsModelAbstract;
import br.com.uwant.models.cloud.models.ListCommentsModelAbstract;
import br.com.uwant.models.cloud.models.ShareModelAbstract;
import br.com.uwant.models.cloud.models.WantModelAbstract;

public class FeedsFragment extends Fragment implements View.OnClickListener,
        IRequest.OnRequestListener<List<Action>>, PopupMenu.OnMenuItemClickListener {

    public static final String TAG = "feedsFragment";
    private static final int DEFAULT_START_INDEX = 0;
    private static final int DEFAULT_END_INDEX = 20;
    private static final FeedsModelAbstract MODEL = new FeedsModelAbstract();

    private Action mActionSelected;
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

    private IRequest.OnRequestListener<Boolean> mListenerReport = new IRequest.OnRequestListener<Boolean>() {

        private ProgressFragmentDialog mProgressDialog;

        @Override
        public void onPreExecute() {
            mProgressDialog = ProgressFragmentDialog.show(getFragmentManager());
        }

        @Override
        public void onExecute(Boolean result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            Toast.makeText(getActivity(), "A atividade foi reportada com sucesso.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(RequestError error) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
        }

    };

    private IRequest.OnRequestListener<Boolean> mListenerExcludeBlock = new IRequest.OnRequestListener<Boolean>() {

        @Override
        public void onPreExecute() {

        }

        @Override
        public void onExecute(Boolean result) {
            updateFeeds();
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
        updateFeeds();
    }

    private void updateFeeds() {
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
        Action action = null;
        if (position != null)
            action = mFeedsAdapter.getItem(position);

        switch (view.getId()) {
            case R.id.adapter_feeds_button_uwants:
                WantModelAbstract wantModel = new WantModelAbstract();
                wantModel.setAction(action);

                Requester.executeAsync(wantModel, LISTENER_WANT);
                toggleWant(action);
                break;

            case R.id.adapter_feeds_button_comments:
                listComments(action);
                break;

            case R.id.adapter_feeds_button_shares:
                ShareModelAbstract shareModel = new ShareModelAbstract();
                shareModel.setAction(action);

                Requester.executeAsync(shareModel, LISTENER_SHARE);
                toggleShare(action);
                break;

            case R.id.adapter_feeds_imageButton:
                openPopUp(view, action);
                break;

            default:
                break;
        }
    }

    private void openPopUp(View v, Action action) {
        this.mActionSelected = action;

        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.feed_actions, popup.getMenu());
        popup.show();
    }

    private void listComments(Action action) {
        ListCommentsModelAbstract model = new ListCommentsModelAbstract();
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (this.mActionSelected != null) {
            switch (item.getGroupId()) {
                case R.id.group_action:
                    switch (item.getItemId()) {
                        case R.id.menu_report:
                            report();
                            break;
                    }
                    return true;

                case R.id.group_friend:
                    switch (item.getItemId()) {
                        case R.id.menu_activities:
                            cancelActivities();
                            break;

                        case R.id.menu_exclude:
                            excludeFriend();
                            break;
                    }
                    return true;
            }
        }
        return false;
    }

    private void excludeFriend() {
        Person person = this.mActionSelected.getFrom();
        ExcludeFriendModelAbstract model = new ExcludeFriendModelAbstract();
        model.setPerson(person);
        Requester.executeAsync(model, this.mListenerExcludeBlock);
    }

    private void cancelActivities() {
        Person person = this.mActionSelected.getFrom();
        BlockFriendModelAbstract model = new BlockFriendModelAbstract();
        model.setPerson(person);
        Requester.executeAsync(model, this.mListenerExcludeBlock);
    }

    private void report() {
        ActionReportModelAbstract model = new ActionReportModelAbstract();
        model.setAction(this.mActionSelected);
        Requester.executeAsync(model, this.mListenerReport);
    }

}

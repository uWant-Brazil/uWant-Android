package br.com.uwant.flow.fragments;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.ContactsActivity;
import br.com.uwant.models.adapters.FeedsAdapter;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.ActionReportModel;
import br.com.uwant.models.cloud.models.BlockFriendModel;
import br.com.uwant.models.cloud.models.ExcludeFriendModel;
import br.com.uwant.models.cloud.models.FeedsModel;
import br.com.uwant.models.cloud.models.ListCommentsModel;
import br.com.uwant.models.cloud.models.ShareModel;
import br.com.uwant.models.cloud.models.WantModel;

public class FeedsFragment extends Fragment implements View.OnClickListener,
        IRequest.OnRequestListener<List<Action>>, PopupMenu.OnMenuItemClickListener {

    public static final String TAG = "feedsFragment";
    private static final int DEFAULT_START_INDEX = 0;
    private static final int DEFAULT_END_INDEX = 20;
    private static final int RQ_ADD_CONTACTS = 2139;

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
    private final IRequest.OnRequestListener<Boolean> LISTENER_REPORT = new IRequest.OnRequestListener<Boolean>() {

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

            Toast.makeText(getActivity(), R.string.text_report_activity, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(RequestError error) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
        }

    };
    private final IRequest.OnRequestListener<Boolean> LISTENER_EXCLUDE_BLOCK = new IRequest.OnRequestListener<Boolean>() {
    private Action mActionSelected;
    private List<Action> mActions;
    private FeedsAdapter mFeedsAdapter;
    private GridView mGridView;


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

    private List<Action> mActions;
    private Action mActionSelected;
    private WishList mWishList;
    private Person mPerson;
    private FeedsAdapter mFeedsAdapter;

    private GridView mGridView;

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
        mGridView.setEmptyView(view.findViewById(R.id.contacts_gridView_loading));
        mGridView.setNumColumns(1);
        mGridView.setAdapter(mFeedsAdapter);

        final ImageView imageViewEmpty = (ImageView) view.findViewById(R.id.feed_imageView_empty);
        imageViewEmpty.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFeeds();
    }

    private void updateFeeds() {
        mGridView.setEmptyView(getView().findViewById(R.id.contacts_gridView_loading));

        mActions.clear();
        mFeedsAdapter.notifyDataSetChanged();

        FeedsModel model = new FeedsModel();
        model.setWishList(this.mWishList);
        model.setPerson(this.mPerson);
        model.setStartIndex(DEFAULT_START_INDEX);
        model.setEndIndex(DEFAULT_END_INDEX);

        Requester.executeAsync(model, this);
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute(List<Action> result) {
        if (result != null && result.size() > 0) {
            mActions.addAll(result);
        } else {
            mGridView.setEmptyView(getView().findViewById(R.id.feed_linearLayout_empty));
        }
        mFeedsAdapter.notifyDataSetChanged();
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

                Requester.executeAsync(shareModel, LISTENER_SHARE);
                toggleShare(action);
                break;

            case R.id.adapter_feeds_imageButton:
                openPopUp(view, action);
                break;

            case R.id.feed_imageView_empty:
                Intent intent = new Intent(getActivity(), ContactsActivity.class);
                intent.putExtra(User.EXTRA_ADD_CONTACTS, true);
                startActivityForResult(intent, RQ_ADD_CONTACTS);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_ADD_CONTACTS && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getActivity(), R.string.text_perfil_friends_circle_add_contacts, Toast.LENGTH_SHORT).show();
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
        ListCommentsModel model = new ListCommentsModel();
        model.setAction(action);

        Requester.executeAsync(model, LISTENER_COMMENTS);
    }

    private void toggleShare(Action action) {
        boolean isShared = action.isuShare();
        if (!isShared) {
            int count = action.getSharesCount();
            if (count >= 0) {
                action.setSharesCount(++count);
                action.setuShare(true);
                mFeedsAdapter.notifyDataSetChanged();
            }
        }
    }

    private void toggleWant(Action action) {
        boolean isWanted = action.isuWant();
        int count = action.getUWantsCount();
        if (!isWanted || count > 0) { // Equivalente a [!isWanted || (count > 0 && isWanted)]
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
        ExcludeFriendModel model = new ExcludeFriendModel();
        model.setPerson(person);
        Requester.executeAsync(model, this.LISTENER_EXCLUDE_BLOCK);
    }

    private void cancelActivities() {
        Person person = this.mActionSelected.getFrom();
        BlockFriendModel model = new BlockFriendModel();
        model.setPerson(person);
        Requester.executeAsync(model, this.LISTENER_EXCLUDE_BLOCK);
    }

    private void report() {
        ActionReportModel model = new ActionReportModel();
        model.setAction(this.mActionSelected);
        Requester.executeAsync(model, this.LISTENER_REPORT);
    }

    public static FeedsFragment newInstance(WishList wishList) {
        FeedsFragment f = new FeedsFragment();
        f.setWishList(wishList);
        return f;
    }

    public static FeedsFragment newInstance(Person person) {
        FeedsFragment f = new FeedsFragment();
        f.setPerson(person);
        return f;
    }

    public void setWishList(WishList wishList) {
        this.mWishList = wishList;
    }

    public void setPerson(Person person) {
        this.mPerson = person;
    }
}

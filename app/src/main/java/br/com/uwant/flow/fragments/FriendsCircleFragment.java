package br.com.uwant.flow.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.ContactsActivity;
import br.com.uwant.models.adapters.FriendsCircleAdapter;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.BlockFriendModelAbstract;
import br.com.uwant.models.cloud.models.ExcludeFriendModelAbstract;
import br.com.uwant.models.cloud.models.FriendsCircleModelAbstract;

public class FriendsCircleFragment extends Fragment implements IRequest.OnRequestListener<List<Person>>,
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final int RQ_ADD_CONTACTS = 1230;
    private static final FriendsCircleModelAbstract MODEL = new FriendsCircleModelAbstract();

    private List<Person> mFriends;
    private Person mPersonSelected;
    private FriendsCircleAdapter mAdapter;

    private ListView mListView;

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
            updateFriends();
        }

        @Override
        public void onError(RequestError error) {
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mFriends = new ArrayList<Person>(50);
        this.mAdapter = new FriendsCircleAdapter(getActivity(), this.mFriends, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_circle, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.friendsCircle_gridView);
        mListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.view_friends_circle_invite, mListView, false));
        mListView.setEmptyView(view.findViewById(R.id.contacts_gridView_loading));
        mListView.setAdapter(mAdapter); // TODO Adapter correto...
        mListView.setOnItemClickListener(this);
        mListView.setTextFilterEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFriends();
    }

    private void updateFriends() {
        Requester.executeAsync(MODEL, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_ADD_CONTACTS && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getActivity(), R.string.text_perfil_friends_circle_add_contacts, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute(List<Person> result) {
        mFriends.clear();
        mFriends.addAll(result);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            Intent intent = new Intent(getActivity(), ContactsActivity.class);
            intent.putExtra(User.EXTRA_ADD_CONTACTS, true);
            startActivityForResult(intent, RQ_ADD_CONTACTS);
        } else {
            // TODO Abrir o perfil do usuário clicado... Qual a diferença?
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mListView != null) {
            if (TextUtils.isEmpty(newText)) {
                mListView.clearTextFilter();
            } else {
                mListView.setFilterText(newText.toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        Person person = null;
        if (position != null)
            person = mAdapter.getItem(position);

        switch (view.getId()) {
            case R.id.friendsCircle_adapter_imageView_popUp:
                openPopUp(view, person);
                break;

            default:
                break;
        }
    }

    private void openPopUp(View v, Person person) {
        this.mPersonSelected = person;

        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.friends_circle_actions, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (this.mPersonSelected != null) {
            switch (item.getGroupId()) {
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
        ExcludeFriendModelAbstract model = new ExcludeFriendModelAbstract();
        model.setPerson(this.mPersonSelected);
        Requester.executeAsync(model, this.mListenerExcludeBlock);
    }

    private void cancelActivities() {
        BlockFriendModelAbstract model = new BlockFriendModelAbstract();
        model.setPerson(this.mPersonSelected);
        Requester.executeAsync(model, this.mListenerExcludeBlock);
    }

}

package br.com.uwant.flow.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
import br.com.uwant.models.cloud.models.FriendsCircleModel;

public class FriendsCircleFragment extends Fragment implements IRequest.OnRequestListener<List<Person>>, AdapterView.OnItemClickListener {

    private static final int RQ_ADD_CONTACTS = 1230;

    private List<Person> mFriends;
    private FriendsCircleAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mFriends = new ArrayList<Person>(50);
        this.mAdapter = new FriendsCircleAdapter(getActivity(), this.mFriends);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_circle, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView gridView = (ListView) view.findViewById(R.id.friendsCircle_gridView);
        gridView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.view_friends_circle_invite, gridView, false));
        gridView.setEmptyView(view.findViewById(R.id.contacts_gridView_loading));
        gridView.setAdapter(mAdapter); // TODO Adapter correto...
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Requester.executeAsync(new FriendsCircleModel(),this);
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
}

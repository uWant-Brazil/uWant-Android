package br.com.uwant.flow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.BaseAdapter;

import java.util.List;

import br.com.uwant.models.adapters.ContactsAdapter;
import br.com.uwant.models.classes.Person;

public class FacebookFragment extends ContactsFragment {

    private ContactsAdapter mAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mAdapter = new ContactsAdapter(getActivity(), this.mGridView, this.mPersons);
    }

    @Override
    protected void loadPersons() {
        // Do nothing...
    }

    @Override
    protected BaseAdapter getAdapter() {
        return this.mAdapter;
    }

    public static FacebookFragment newInstance(List<Person> mFacebookPersons) {
        FacebookFragment ff = new FacebookFragment();
        ff.mPersons = mFacebookPersons;
        return ff;
    }
}

package br.com.uwant.flow.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.views.CheckableLinearLayout;

public abstract class ContactsFragment extends Fragment implements AdapterView.OnItemClickListener, Runnable {

    protected List<Person> mPersons = new ArrayList<Person>(100);
    protected GridView mGridView;
    private boolean mIsLoading;
    private AsyncTask<Void, Void, Void> mTask;
    private View mEmptyView;
    private View mLoadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mGridView = (GridView) view.findViewById(R.id.contacts_gridView);
        this.mGridView.setEmptyView(view.findViewById(R.id.contacts_gridView_loading));
        this.mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        this.mGridView.setOnItemClickListener(this);

        this.mLoadingView = view.findViewById(R.id.contacts_gridView_loading);
        this.mEmptyView = view.findViewById(R.id.contacts_gridView_empty);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.loadAsync();
    }

    private void loadAsync() {
        final BaseAdapter baseAdapter = getAdapter();
        if (baseAdapter != null) {
            mGridView.setAdapter(baseAdapter);

            if (!this.mIsLoading && this.mPersons != null && this.mPersons.size() == 0) {
                this.mIsLoading = true;
                this.mTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        loadPersons();
                        Collections.sort(mPersons, new Comparator<Person>() {

                            @Override
                            public int compare(Person p1, Person p2) {
                                return p1.getName().compareToIgnoreCase(p2.getName());
                            }

                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (mPersons.size() > 0) {
                            baseAdapter.notifyDataSetChanged();
                        } else {
                            mGridView.setEmptyView(mEmptyView);
                            mLoadingView.setVisibility(View.GONE);
                        }
                        mIsLoading = false;
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                if (mPersons != null && mPersons.size() > 0) {
                    baseAdapter.notifyDataSetChanged();
                    mIsLoading = false;
                } else {
                    mGridView.setEmptyView(mEmptyView);
                    mLoadingView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            if (view instanceof CheckableLinearLayout) {
                CheckableLinearLayout cll = (CheckableLinearLayout) view;
                for (int j = 0;j < adapterView.getAdapter().getCount();j++) {
                    this.mGridView.setItemChecked(j, cll.isChecked());
                }
            }
        }
    }

    public void cancelTask() {
        if (this.mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING && !this.mTask.isCancelled()) {
            this.mTask.cancel(true);
        }
    }

    public boolean isCancelled() {
        return mTask.isCancelled();
    }

    protected abstract void loadPersons();
    protected abstract BaseAdapter getAdapter();

    public boolean hasPersons() {
        return this.mPersons != null && this.mPersons.size() > 0 && !mIsLoading;
    }

    public List<Person> getCheckedContacts() {
        List<Person> contacts = new ArrayList<Person>(this.mPersons.size() / 2);

        SparseBooleanArray sba = this.mGridView.getCheckedItemPositions();
        for (int i = 1;i <= this.mPersons.size();i++) {
            if (sba.get(i, false)) {
                Person person = this.mPersons.get(i - 1);
                contacts.add(person);
            }
        }

        return contacts;
    }

    @Override
    public void run() {
        BaseAdapter adapter = getAdapter();
        adapter.notifyDataSetChanged();
    }
}

package br.com.uwant.flow;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.adapters.FriendsCircleAdapter;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.FriendsCircleModel;

/**
 * Created by cleibson.silva on 17/02/2015.
 */
public class FriendsActivity extends UWActivity implements IRequest.OnRequestListener<List<Person>>{

    private List<Person> mFriends;
    private Person mPersonSelected;
    private FriendsCircleAdapter mAdapter;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_friends);

        mFriends = new ArrayList<Person>(50);
        mListView = (ListView) findViewById(R.id.friends_view);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFriends();
    }

    public void updateFriends() {
        mFriends.clear();

        FriendsCircleModel model = new FriendsCircleModel();
        model.setPerson(super.mUser);
        Requester.executeAsync(model, this);
    }

    @Override
    public void onPreExecute() {
        findViewById(R.id.contacts_gridView_loading).setVisibility(View.GONE);
        findViewById(R.id.feed_linearLayout_empty).setVisibility(View.GONE);

        mListView.setEmptyView(findViewById(R.id.contacts_gridView_loading));
    }

    @Override
    public void onExecute(List<Person> result) {
        if (result != null && result.size() > 0) {
            mFriends.addAll(result);
            mAdapter = new FriendsCircleAdapter(this, mFriends);
            mListView.setAdapter(mAdapter);
        } else {
            mListView.setEmptyView(findViewById(R.id.feed_linearLayout_empty));
        }
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

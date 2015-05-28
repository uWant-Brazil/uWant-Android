package br.com.uwant.flow;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
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
public class FriendsActivity extends UWActivity implements IRequest.OnRequestListener<List<Person>>, MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener {

    private List<Person> mFriends;
    private Person mPersonSelected;
    private FriendsCircleAdapter mAdapter;

    private ListView mListView;
    private SearchView mSearchView;

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
        resetSearchView();
        supportInvalidateOptionsMenu();
    }

    public void resetSearchView() {
        if (mSearchView != null) {
            mSearchView.setQuery("", true);
            mSearchView.clearFocus();
        }
    }

    public void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setIconified(true);
        mSearchView.setQuery("", false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.text_hint_perfil_search));
        resetSearchView();
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
        getMenuInflater().inflate(R.menu.perfil_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_perfil_search);
        searchItem.setOnActionExpandListener(this);
        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_perfil_search:
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setupSearchView();
        MenuItemCompat.collapseActionView(menu.findItem(R.id.menu_perfil_search));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        resetSearchView();
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        resetSearchView();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return onQueryTextChange(s);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mListView != null) {
            Filter f =  mAdapter.getFilter();
            if (TextUtils.isEmpty(newText)) {
                f.filter(null);
            } else {
                f.filter(newText);
            }
            return true;
        }
        return false;
    }
}

package br.com.uwant.flow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.adapters.WishListAdapter;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.WishListModel;

public class WishListFragment extends Fragment implements IRequest.OnRequestListener<List<WishList>>,
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private static final int EMPTY_WISH_LIST_COUNT = 4;

    private List<WishList> mWishLists;
    private WishListAdapter mAdapter;

    private SearchView mSearchView;
    private GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWishLists = new ArrayList<WishList>(5);
        mAdapter = new WishListAdapter(getActivity(), mWishLists);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wish_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGridView = (GridView) view.findViewById(R.id.wishList_gridView);
        mGridView.setEmptyView(view.findViewById(R.id.contacts_gridView_loading));
        mGridView.setAdapter(mAdapter); // TODO Adapter correto...
        mGridView.setOnItemClickListener(this);
        mGridView.setTextFilterEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Requester.executeAsync(new WishListModel(), this);
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute(List<WishList> result) {
        mWishLists.clear();

        if (result.size() == 0) {
            final String emptyDefaultTitle = getString(R.string.text_wish_list_empty_default_title);
            final WishList wishListDefault = new WishList(WishList.EMPTY_DEFAULT_ID, emptyDefaultTitle);
            mWishLists.add(wishListDefault);

            final String emptyTitle = getString(R.string.text_wish_list_empty_title);
            for (int i = 0;i < EMPTY_WISH_LIST_COUNT - 1;i++) {
                final WishList wishList = new WishList(WishList.EMPTY_ID, emptyTitle);
                mWishLists.add(wishList);
            }
        } else {
            mWishLists.addAll(result);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // TODO O que acontece ao clicar?
//        WishList wishList = this.mWishLists.get(i);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mGridView != null) {
            if (TextUtils.isEmpty(newText)) {
                mGridView.clearTextFilter();
            } else {
                mGridView.setFilterText(newText.toString());
            }
            return true;
        }
        return false;
    }
}
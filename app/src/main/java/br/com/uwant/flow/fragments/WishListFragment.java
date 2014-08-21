package br.com.uwant.flow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
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
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.WishListDeleteModel;
import br.com.uwant.models.cloud.models.WishListModel;

public class WishListFragment extends Fragment implements IRequest.OnRequestListener<List<WishList>>,
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final int EMPTY_WISH_LIST_COUNT = 4;
    private static final WishListModel MODEL = new WishListModel();

    private List<WishList> mWishLists;
    private WishList mWishListSelected;
    private WishListAdapter mAdapter;

    private GridView mGridView;
    private ProgressFragmentDialog mProgressDialog;

    private final IRequest.OnRequestListener<Boolean> LISTENER_POPUP = new IRequest.OnRequestListener<Boolean>() {
        @Override
        public void onPreExecute() {
            mProgressDialog = ProgressFragmentDialog.show(getFragmentManager());
        }

        @Override
        public void onExecute(Boolean result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            updateWishList();
        }

        @Override
        public void onError(RequestError error) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWishLists = new ArrayList<WishList>(5);
        mAdapter = new WishListAdapter(getActivity(), mWishLists, this);
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
        updateWishList();
    }

    private void updateWishList() {
        Requester.executeAsync(MODEL, this);
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

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        WishList wishList = null;
        if (position != null)
            wishList = mAdapter.getItem(position);

        switch (view.getId()) {
            case R.id.adapter_wishlist_imageView_popup:
                openPopUp(view, wishList);
                break;

            default:
                break;
        }
    }

    private void openPopUp(View v, WishList wishList) {
        this.mWishListSelected = wishList;

        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.wishlist_actions, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (mWishListSelected != null) {
            switch (menuItem.getGroupId()) {
                case R.id.group_action:
                    switch (menuItem.getItemId()) {
                        case R.id.menu_edit:
                            edit();
                            break;

                        case R.id.menu_delete:
                            delete();
                            break;

                        case R.id.menu_share:
                            share();
                            break;

                        case R.id.menu_report:
                            report();
                            break;

                        default:
                    }
                    break;

                default:
                    break;
            }
        }
        return false;
    }

    private void report() {
        // TODO ...
    }

    private void share() {
        // TODO ...
    }

    private void delete() {
        WishListDeleteModel model = new WishListDeleteModel();
        model.setWishList(this.mWishListSelected);
        Requester.executeAsync(model, LISTENER_POPUP);
    }

    private void edit() {
        // TODO ...
    }

}

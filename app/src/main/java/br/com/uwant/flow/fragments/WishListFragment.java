package br.com.uwant.flow.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.WishListActivity;
import br.com.uwant.models.adapters.WishListAdapter;
import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.FeedsModel;
import br.com.uwant.models.cloud.models.WishListDeleteModel;
import br.com.uwant.models.cloud.models.WishListModel;
import br.com.uwant.models.cloud.models.WishListProductsModel;
import br.com.uwant.utils.PictureUtil;
import br.com.uwant.utils.WishListUtil;

public class WishListFragment extends Fragment implements IRequest.OnRequestListener<List<WishList>>,
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final int EMPTY_WISH_LIST_COUNT = 4;
    private static final int DEFAULT_START_INDEX = 0;
    private static final int DEFAULT_END_INDEX = 20;

    private boolean mIsFeed;
    private List<WishList> mWishLists;
    private WishList mWishListSelected;
    private WishListAdapter mAdapter;
    private Person mPerson;

    private GridView mGridView;
    private ImageView mImageViewPictureDetail;
    private ImageView mImageViewPicture;
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

            refresh();
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
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                FrameLayout frameLayout = (FrameLayout) getView().findViewById(R.id.wishList_frameLayout_grid);
                frameLayout.setVisibility(frameLayout.isShown() ? View.INVISIBLE : View.VISIBLE);
            }

        });

        mWishLists = new ArrayList<WishList>(5);
        mAdapter = new WishListAdapter(getActivity(), mWishLists, this, this.mPerson);
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

        final ImageView imageFeed = (ImageView) view.findViewById(R.id.perfil_imageView_feed);
        imageFeed.setOnClickListener(this);

        mImageViewPicture = (ImageView) view.findViewById(R.id.perfil_imageView_picture);
        mImageViewPictureDetail = (ImageView) view.findViewById(R.id.perfil_imageView_pictureDetail);

        String name = mPerson.getName();
        final TextView textViewName = (TextView) view.findViewById(R.id.perfil_textView_name);
        textViewName.setText(name);

        updatePicture();

        final ImageButton buttonCreate = (ImageButton) view.findViewById(R.id.wishList_imageButton_create);
        buttonCreate.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
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

            for (int i = 0;i < result.size();i++) {
                final int index = i;
                final WishList wl = result.get(i);

                IRequest.OnRequestListener<List<Product>> listener = new IRequest.OnRequestListener<List<Product>>() {

                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onExecute(List<Product> result) {
                        wl.setProducts(result);
                        if (index <= 2 || index % 3 == 0) {
                            mAdapter.notifyDataSetChanged();
                        }

                        WishListUtil.loadPicturesFromProducts(getResources(), mAdapter, wl);
                    }

                    @Override
                    public void onError(RequestError error) {

                    }

                };

                WishListProductsModel model = new WishListProductsModel();
                model.setWishList(wl);
                if (isMyself()) {
                    Requester.executeAsync(getActivity(), model, listener);
                } else {
                    Requester.executeAsync(model, listener);
                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        WishList wishListSelected = mAdapter.getItem(i);
        if (wishListSelected != null) {
            FeedsFragment f = FeedsFragment.newInstance(wishListSelected);
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top, R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom)
                    .replace(android.R.id.content, f, FeedsFragment.TAG)
                    .addToBackStack(FeedsFragment.TAG)
                    .commit();
        }
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

            case R.id.wishList_imageButton_create:
                Intent it = new Intent(getActivity(), WishListActivity.class);
                startActivity(it);
                break;

            case R.id.perfil_imageView_feed:
                toggle();
                break;

            default:
                break;
        }
    }

    private void toggle() {
        this.mIsFeed = !this.mIsFeed;
        if (!this.mIsFeed)
            getFragmentManager().popBackStackImmediate();

        refresh();
    }

    private void refresh() {
        if (this.mIsFeed) {
            // Feed...
            updateFeed();
        } else {
            // Wishlist...
            updateWishList();
        }
    }

    private void updateFeed() {
        FeedsFragment f = FeedsFragment.newInstance(this.mPerson);
        getFragmentManager().beginTransaction()
                .replace(R.id.wishList_frameLayout, f, FeedsFragment.TAG)
                .addToBackStack(FeedsFragment.TAG)
                .commit();
    }

    private void updateWishList() {
        WishListModel model = new WishListModel();
        model.setPerson(this.mPerson);
        if (isMyself()) {
            Requester.executeAsync(getActivity(), model, this);
        } else {
            Requester.executeAsync(model, this);
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
        Intent it = new Intent(getActivity(), WishListActivity.class);
        WishList wishList = this.mWishListSelected;

        for (int i = 0; i < wishList.getProducts().size() ; i++) {
            wishList.getProducts().get(i).getPicture().setBitmap(null);
            wishList.getProducts().get(i).getPicture().setUri(null);
        }

        it.putExtra(WishList.EXTRA, wishList);
        it.putExtra("MODE" , WishListActivity.EXTRA_MODE.EDIT.ordinal());
        startActivity(it);
    }

    public static Fragment newInstance(Person person) {
        WishListFragment f = new WishListFragment();
        f.setPerson(person);
        return f;
    }

    public void setPerson(Person person) {
        this.mPerson = person;
    }

    public Person getPerson() {
        return mPerson;
    }

    private boolean isMyself() {
        return (this.mPerson instanceof User);
    }

    public void updatePicture() {
        Multimedia multimedia = mPerson.getPicture();
        if (multimedia != null) {
            Bitmap bitmap = multimedia.getBitmap();
            if (bitmap != null) {
                mImageViewPicture.setImageBitmap(bitmap);
                mImageViewPictureDetail.setVisibility(View.VISIBLE);
            } else {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(multimedia.getUrl(), mImageViewPicture, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        mImageViewPicture.setImageResource(R.drawable.ic_semfoto);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        mImageViewPicture.setImageResource(R.drawable.ic_semfoto);
                        mImageViewPictureDetail.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                        bitmap = PictureUtil.cropToFit(bitmap);
                        bitmap = PictureUtil.scale(bitmap, mImageViewPicture);
                        bitmap = PictureUtil.circle(bitmap);
                        mImageViewPicture.setImageBitmap(bitmap);
                        mImageViewPictureDetail.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        mImageViewPicture.setImageResource(R.drawable.ic_semfoto);
                        mImageViewPictureDetail.setVisibility(View.INVISIBLE);
                    }

                });
            }
        }
    }
}

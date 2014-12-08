package br.com.uwant.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.utils.WishListUtil;

public class WishListAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private final List<WishList> mWishLists;
    private boolean itsMe;
    private List<WishList> mFilteredWishLists;
    private Filter mFilter;
    private View.OnClickListener mPopUpListener;

    public WishListAdapter(Context context, List<WishList> wishLists, View.OnClickListener listener, Person whoAmI) {
        this.mContext = context;
        this.mWishLists = wishLists;
        this.mPopUpListener = listener;
        this.itsMe = (whoAmI instanceof User);
    }

    @Override
    public int getCount() {
        return this.mFilteredWishLists == null ?
                (this.mWishLists != null ? this.mWishLists.size() : 0)
                :
                (this.mFilteredWishLists.size());
    }

    @Override
    public WishList getItem(int i) {
        return this.mFilteredWishLists == null ?
                (this.mWishLists != null ? this.mWishLists.get(i) : null)
                :
                (this.mFilteredWishLists.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_wish_list, viewGroup, false);
            holder.hTextViewTitle = (TextView) view.findViewById(R.id.adapter_wishlist_textView_title);
            holder.hGridLayoutPictures = (GridLayout) view.findViewById(R.id.adapter_wishlist_gridlayout_pictures);
            holder.hProgressBar = (ProgressBar) view.findViewById(R.id.adapter_wishlist_progressBar);
            holder.hImageViewProducts = (ImageView) view.findViewById(R.id.adapter_wishlist_imageView_products);
            holder.hImageViewPopUp = (ImageView) view.findViewById(R.id.adapter_wishlist_imageView_popup);

            holder.hImageViewPopUp.setOnClickListener(this.mPopUpListener);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        WishList wishList = getItem(i);
        long id = wishList.getId();
        String title = wishList.getTitle();
        List<Product> products = wishList.getProducts();

        holder.hTextViewTitle.setText(title);
        if ((id == WishList.EMPTY_ID) && holder.hImageViewProducts.getVisibility() != View.VISIBLE) {
            holder.hImageViewProducts.setVisibility(View.VISIBLE);
        } else if (products != null && products.size() > 0) {
            WishListUtil.renderProducts(this.mContext, products, holder.hGridLayoutPictures);

            if (holder.hProgressBar.getVisibility() == View.VISIBLE) {
                holder.hProgressBar.setVisibility(View.GONE);
            }
            if (holder.hImageViewProducts.getVisibility() == View.VISIBLE) {
                holder.hImageViewProducts.setVisibility(View.GONE);
            }
        }

        if (itsMe) {
            if (holder.hImageViewPopUp.getVisibility() != View.VISIBLE) {
                if (wishList.getId() != WishList.EMPTY_ID) {
                    holder.hImageViewPopUp.setVisibility(View.VISIBLE);
                }
            }

            holder.hImageViewPopUp.setTag(i);
        } else {
            if (holder.hImageViewPopUp.getVisibility() == View.VISIBLE) {
                holder.hImageViewPopUp.setVisibility(View.GONE);
            }
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            createFilter();
        }
        return mFilter;
    }

    private void createFilter() {
        mFilter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<WishList> values = (List<WishList>) results.values;
                if (values != null) {
                    mFilteredWishLists = values; // has the filtered values
                } else {
                    mFilteredWishLists = null;
                }

                notifyDataSetChanged();  // notifies the data with new filtered values. Only filtered values will be shown on the list
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<WishList> filteredWishList = new ArrayList<WishList>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = 0;
                    results.values = null;
                } else {
                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < mWishLists.size(); i++) {
                        WishList data = mWishLists.get(i);
                        if (data.getTitle().toLowerCase().contains(constraint.toString())) {
                            filteredWishList.add(data);
                        }
                    }

                    results.count = filteredWishList.size();
                    results.values = filteredWishList;
                }
                return results;
            }
        };
    }

    private static class ViewHolder {
        TextView hTextViewTitle;
        GridLayout hGridLayoutPictures;
        ProgressBar hProgressBar;
        ImageView hImageViewProducts;
        ImageView hImageViewPopUp;
    }
}

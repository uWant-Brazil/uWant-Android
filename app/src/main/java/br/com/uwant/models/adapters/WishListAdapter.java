package br.com.uwant.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;

public class WishListAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private final List<WishList> mWishLists;
    private List<WishList> mFilteredWishLists;
    private Filter mFilter;
    private View.OnClickListener mListener;

    public WishListAdapter(Context context, List<WishList> wishLists, View.OnClickListener listener) {
        this.mContext = context;
        this.mWishLists = wishLists;
        this.mListener = listener;
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
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_wish_list, viewGroup, false);
            holder.hTextViewTitle = (TextView) view.findViewById(R.id.adapter_wishlist_textView_title);
            holder.hImageViewProducts = (ImageView) view.findViewById(R.id.adapter_wishlist_imageView_products);
            holder.hImageViewPopUp = (ImageView) view.findViewById(R.id.adapter_wishlist_imageView_popup);
            holder.hImageViewPopUp.setOnClickListener(this.mListener);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.hImageViewPopUp.setTag(i);

        WishList wishList = getItem(i);
        long id = wishList.getId();
        String title = wishList.getTitle();
        List<Product> products = wishList.getProducts();

        holder.hTextViewTitle.setText(title);
        if (id == WishList.EMPTY_ID || products == null || products.size() == 0) {
            // FIXME Trocar a imagem pela correta.
            holder.hImageViewProducts.setImageResource(R.drawable.ic_perfil_semfoto);
        } else {
            // TODO Como irei gerar as imagens??? :)
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
        ImageView hImageViewProducts;
        ImageView hImageViewPopUp;
    }

}

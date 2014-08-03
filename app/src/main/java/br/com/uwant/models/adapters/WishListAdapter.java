package br.com.uwant.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;

/**
 * Created by felipebenezi on 02/08/14.
 */
public class WishListAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<WishList> mWishLists;

    public WishListAdapter(Context context, List<WishList> wishLists) {
        this.mContext = context;
        this.mWishLists = wishLists;
    }

    @Override
    public int getCount() {
        return this.mWishLists != null ? this.mWishLists.size() : 0;
    }

    @Override
    public WishList getItem(int i) {
        return this.mWishLists != null ? this.mWishLists.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            // TODO Verificar se com o ViewHolder ficaria melhor!!!!
            view = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_wish_list, viewGroup, false);
        }

        final TextView textViewTitle = (TextView) view.findViewById(R.id.wishList_adapter_textView_title);
        final ImageView imageViewProducts = (ImageView) view.findViewById(R.id.wishList_adapter_imageView_products);

        WishList wishList = getItem(i);
        long id = wishList.getId();
        String title = wishList.getTitle();
        List<Product> products = wishList.getProducts();

        textViewTitle.setText(title);
        if (id == WishList.EMPTY_ID || products == null || products.size() == 0) {
            // FIXME Trocar a imagem pela correta.
            imageViewProducts.setImageResource(R.drawable.ic_launcher);
        } else {
            // TODO Como irei gerar as imagens??? :)
        }

        return view;
    }

}

package br.com.uwant.flow.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import br.com.uwant.R;
import br.com.uwant.flow.WishListActivity;

public class WishListButtonFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "wishlistButtonFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist_button, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageButton imageButton = (ImageButton) view.findViewById(R.id.wishListButton_main_create);
        imageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wishListButton_main_create:
                Intent it = new Intent(getActivity(), WishListActivity.class);
                startActivity(it);
                break;

            default:
                break;
        }
    }
}

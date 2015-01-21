package br.com.uwant.models.watchers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.WishListsSearchModel;

/**
 * Created by Felipe Bonezi on 21/01/2015.
 */
public class WishListWatcher implements TextWatcher, IRequest.OnRequestListener<List<WishList>>, AdapterView.OnItemClickListener {

    private boolean mIsUpdating;
    private Context mContext;
    private AutoCompleteTextView mAutoTextView;
    private List<WishList> mWishLists;

    public WishListWatcher(Context context, AutoCompleteTextView mEditTextWishList) {
        this.mContext = context;
        this.mAutoTextView = mEditTextWishList;
        this.mWishLists = new ArrayList<WishList>(5);

        this.mAutoTextView.setOnItemClickListener(this);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onExecute(List<WishList> result) {
        if (result != null && result.size() > 0) {
            this.mWishLists.clear();
            this.mWishLists.addAll(result);

            if (this.mAutoTextView.isPopupShowing()) {
                this.mAutoTextView.dismissDropDown();
            }

            ArrayAdapter<WishList> adapter = new ArrayAdapter<WishList>(this.mContext, android.R.layout.simple_list_item_1, this.mWishLists);
            this.mAutoTextView.setAdapter(adapter);
            this.mAutoTextView.showDropDown();
        }
    }

    @Override
    public void onError(RequestError error) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int index = this.mAutoTextView.getListSelection();
        if (this.mIsUpdating || s.length() < 3 || (index >= 0 && this.mWishLists.get(index).getTitle().equals(s.toString()))) {
            this.mIsUpdating = false;
            return;
        }

        if (this.mAutoTextView.isPopupShowing()) {
            this.mAutoTextView.dismissDropDown();
        }

        WishListsSearchModel model = new WishListsSearchModel();
        model.setWishListName(s.toString());
        Requester.executeAsync(model, this);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.mIsUpdating = true;

        if (this.mAutoTextView.isPopupShowing()) {
            this.mAutoTextView.dismissDropDown();
        }
    }
}

package br.com.uwant.models.watchers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.ManufacturersModel;

public class ManufacturerWatcher implements TextWatcher, IRequest.OnRequestListener<List<Manufacturer>>, AdapterView.OnItemClickListener {

    private boolean mIsUpdating;
    private boolean mIsFilled;
    private Context mContext;
    private AutoCompleteTextView mAutoTextView;
    private ArrayAdapter<Manufacturer> mAdapter;
    private List<Manufacturer> mManufacturers;

    public ManufacturerWatcher(Context context, AutoCompleteTextView multiAutoCompleteTextView) {
        this.mContext = context;
        this.mAutoTextView = multiAutoCompleteTextView;
        this.mManufacturers = new ArrayList<Manufacturer>(5);

        this.mAutoTextView.setOnItemClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mIsFilled = !TextUtils.isEmpty(s);
        if (mIsFilled) {
            this.mAutoTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_post_campo_loja_on, 0);
        } else {
            this.mAutoTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_post_campo_loja, 0);
        }

        int index = this.mAutoTextView.getListSelection();
        if (this.mIsUpdating || s.length() < 3 || (index >= 0 && this.mManufacturers.get(index).getName().equals(s.toString()))) {
            this.mIsUpdating = false;
            return;
        }

        if (this.mAutoTextView.isPopupShowing()) {
            this.mAutoTextView.dismissDropDown();
        }

        ManufacturersModel model = new ManufacturersModel();
        model.setManufacturerName(s.toString());
        Requester.executeAsync(model, this);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onExecute(List<Manufacturer> result) {
        if (result != null && result.size() > 0 && !this.mIsUpdating) {
            this.mManufacturers.clear();
            this.mManufacturers.addAll(result);

            this.mAdapter = new ArrayAdapter<Manufacturer>(this.mContext, android.R.layout.simple_list_item_1, this.mManufacturers);

            if (this.mAutoTextView.isPopupShowing()) {
                this.mAutoTextView.dismissDropDown();
            }

            this.mAutoTextView.setAdapter(this.mAdapter);
            this.mAutoTextView.showDropDown();
        }
    }

    @Override
    public void onError(RequestError error) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.mIsUpdating = true;

        if (this.mAutoTextView.isPopupShowing()) {
            this.mAutoTextView.dismissDropDown();
        }
    }
}

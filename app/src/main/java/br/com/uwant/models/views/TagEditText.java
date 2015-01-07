package br.com.uwant.models.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.errors.RequestError;

public class TagEditText extends EditText implements IRequest.OnRequestListener<List<Person>>,
        AdapterView.OnItemClickListener {

    private List<Person> mPersons;
    private TagPopup mPopup;
    private TagWatcher mWatcher;

    public TagEditText(Context context) {
        super(context);
        configure();
    }

    public TagEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure();
    }

    public TagEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        configure();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPopup.dismiss();

        Person person = mPersons.get(position);
        mWatcher.tag(person);
        
        mPersons.clear();
        requestFocus();
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute(List<Person> result) {
        if (result.size() > 0) {
            // Múltipla escolha...
            mPersons.clear();
            mPersons.addAll(result);
            showPopup();
        }
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showPopup() {
        if (this.mPopup == null) {
            this.mPopup = new TagPopup(getContext(), this, mPersons);
        } else {
            this.mPopup.update();
        }
        this.mPopup.show();
    }

    private void configure() {
        mPersons = new ArrayList<Person>(10);
        mWatcher = new TagWatcher(this);

        addTextChangedListener(mWatcher);
    }

}

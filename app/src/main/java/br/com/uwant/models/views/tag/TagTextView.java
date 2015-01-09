package br.com.uwant.models.views.tag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.errors.RequestError;

public class TagTextView extends TextView implements IRequest.OnRequestListener<List<Person>> {

    private List<Person> mPersons;
    private TagWatcher mWatcher;

    public TagTextView(Context context) {
        super(context);
        configure();
    }

    public TagTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure();
    }

    public TagTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        configure();
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute(List<Person> result) {
        if (result.size() > 0) {
            Person person = result.get(0);

            mPersons.clear();
            mPersons.add(person);

            mWatcher.tag(person);
        }
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void configure() {
        mPersons = new ArrayList<Person>(10);
        mWatcher = new TagWatcher(this);

        addTextChangedListener(mWatcher);
    }

}

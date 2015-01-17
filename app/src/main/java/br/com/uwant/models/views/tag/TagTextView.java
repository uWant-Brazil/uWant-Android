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

public class TagTextView extends TextView {

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

    private void configure() {
        mPersons = new ArrayList<Person>(10);
        mWatcher = new TagWatcher(this);

        addTextChangedListener(mWatcher);
    }

}

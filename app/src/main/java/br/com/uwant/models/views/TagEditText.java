package br.com.uwant.models.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.UserSearchModel;

public class TagEditText extends MultiAutoCompleteTextView implements IRequest.OnRequestListener<List<Person>>,
        AdapterView.OnItemClickListener {

    private boolean mTagActive;
    private List<SpannableTagBuilder> mSpannables;
    private List<Person> mPersons;
    private ArrayAdapter<Person> mAdapter;

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

    private void configure() {
        mSpannables = new ArrayList<SpannableTagBuilder>();
        mPersons = new ArrayList<Person>();
        mAdapter = new ArrayAdapter<Person>(getContext(), android.R.layout.simple_list_item_1, mPersons);

        setAdapter(mAdapter);
        setOnItemClickListener(this);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (!TextUtils.isEmpty(text)) {
            boolean isTexting = (lengthAfter - lengthBefore) > 0;
            if (isTexting) {
                // Digitando...
                char last = getSelectionEnd() > 0 ? text.charAt(getSelectionEnd() - 1) : ' ';
                char previousLast = getSelectionEnd() > 1 ? text.charAt(getSelectionEnd() - 2) : ' ';
                if (last == '@' && previousLast == ' ') {
                    mTagActive = true;
                } else if (mTagActive && (last == ' ' || last == '\n' || last == '\r')) {
                    mTagActive = false;

                    if (mPersons.size() > 0) {
                        mPersons.clear();
                        mAdapter.notifyDataSetChanged();
                    }

                    String textAll = text.toString();
                    int lastIndex = textAll.lastIndexOf("@");
                    String sub = textAll.substring(lastIndex);

                    String user = sub.trim();
                    confirmUser(user);
                }
            } else {
                // Apagando...
                checkTags();
            }
        }
    }

    private void confirmUser(String user) {
        UserSearchModel model = new UserSearchModel();
        model.setQuery(user.replaceAll("[@]", ""));
        Requester.executeAsync(model, this);
    }

    public void checkTags() {
        String textAll = getText().toString();
        int startComma = textAll.lastIndexOf("@");

        if (startComma >= 0) {
        } else {
            mTagActive = false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPersons.clear();
        mAdapter.notifyDataSetChanged();

        Person person = mPersons.get(position);

        SpannableTagBuilder mSpannableBuilder = new SpannableTagBuilder(this);
        mSpannables.add(mSpannableBuilder);

        mSpannableBuilder.addTag(mSpannables.indexOf(mSpannableBuilder), 0, 0, person);
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute(List<Person> result) {
        if (result.size() > 1) {
            mPersons.clear();
            mPersons.addAll(result);
            mAdapter.notifyDataSetChanged();
        } else if (result.size() == 1) {
            Person person = result.get(0);
            
            SpannableTagBuilder mSpannableBuilder = new SpannableTagBuilder(this);
            mSpannables.add(mSpannableBuilder);

            mSpannableBuilder.addTag(mSpannables.indexOf(mSpannableBuilder), 0, 0, person);
        }
    }

    @Override
    public void onError(RequestError error) {
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

}

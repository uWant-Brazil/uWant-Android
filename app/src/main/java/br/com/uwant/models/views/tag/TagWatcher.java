package br.com.uwant.models.views.tag;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.models.UserSearchModel;

public class TagWatcher implements TextWatcher {

    private static final Pattern PATTERN = Pattern.compile("(<uwt id='\\d'>(?:(?!<\\/uwt>).)*.*?<\\/uwt>)|(@[0-9a-zA-Z]{3,})");

    private boolean mIsUpdating;
    private int mStartIndex;
    private List<ImageSpan> mSpansToRemove;
    private TextView mTextView;
    private EditText mEditText;
    private IRequest.OnRequestListener<List<Person>> mListener;

    public TagWatcher(TextView textView) {
        this.mIsUpdating = false;
        this.mStartIndex = -1;
        this.mSpansToRemove = new ArrayList<ImageSpan>(10);
        this.mTextView = textView;

        if (textView instanceof IRequest.OnRequestListener) {
            this.mListener = (IRequest.OnRequestListener<List<Person>>) textView;
        } else {
            throw new IllegalArgumentException("Your editable must implements OnRequestListener.");
        }
    }

    public TagWatcher(EditText editText) {
        this.mIsUpdating = false;
        this.mStartIndex = -1;
        this.mSpansToRemove = new ArrayList<ImageSpan>(10);
        this.mEditText = editText;
        this.mTextView = editText;

        if (editText instanceof IRequest.OnRequestListener) {
            this.mListener = (IRequest.OnRequestListener<List<Person>>) editText;
        } else {
            throw new IllegalArgumentException("Your editable must implements OnRequestListener.");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (count > 0) {
            int end = start + count;
            Editable message = this.mTextView.getEditableText();
            ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

            for (ImageSpan span : list) {
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);

                if ((spanStart < end) && (spanEnd > start)) {
                    this.mSpansToRemove.add(span);
                }
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable message = this.mTextView.getEditableText();

        for (ImageSpan span : this.mSpansToRemove) {
            int startI = message.getSpanStart(span);
            int endI = message.getSpanEnd(span);

            message.removeSpan(span);

            if (startI != endI) {
                message.delete(startI, endI);
            }
        }
        this.mSpansToRemove.clear();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (this.mIsUpdating) {
            this.mIsUpdating = false;
            return;
        }

        Matcher matcher = PATTERN.matcher(s);
        while (matcher.find()) {
            String tagged = matcher.group(1);
            String untagged = matcher.group(2);

            if (tagged != null && this.mEditText == null) {
                this.mIsUpdating = true;

                this.mStartIndex = matcher.start(1);

                long id = Long.parseLong(tagged.substring(tagged.indexOf("'") + 1, tagged.lastIndexOf("'")));
                String login = tagged.substring(tagged.indexOf("@") + 1, tagged.indexOf("</uwt>")).trim();

                Person person = new Person();
                person.setId(id);
                person.setLogin(login);

                List<Person> persons = new ArrayList<Person>();
                persons.add(person);

                this.mListener.onExecute(persons);
            } if (untagged != null && this.mEditText != null) {
                this.mIsUpdating = true;

                this.mStartIndex = matcher.start(2);
                requestUsers(untagged);
            }
        }
    }

    public void tag(Person person) {
        int length = person.getLogin().length();

        SpannableTagBuilder spannable = new SpannableTagBuilder(this.mTextView);
        spannable.addTag(person.getId(), this.mStartIndex, (this.mStartIndex + length) + 1, person);
    }

    private void requestUsers(String user) {
        UserSearchModel model = new UserSearchModel();
        model.setQuery(user.replaceAll("[@]", ""));
        Requester.executeAsync(model, this.mListener);
    }

}

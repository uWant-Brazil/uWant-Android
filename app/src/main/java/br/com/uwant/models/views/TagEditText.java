package br.com.uwant.models.views;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.UserSearchModel;
import br.com.uwant.utils.DebugUtil;

public class TagEditText extends EditText implements IRequest.OnRequestListener<List<Person>>,
        AdapterView.OnItemClickListener {

    private List<Person> mPersons;
    private Map<Person, Pair<Integer, Integer>> mPersonPositionMap;
    private TagPopup mPopup;
    private TextWatcher mWatcher = new TextWatcher() {

        private boolean wIsUpdating;
        private Pattern wTagPattern = Pattern.compile("(<uwt id='\\d'>(?:(?!<\\/uwt>).)*.*?<\\/uwt>)|(@[0-9a-zA-Z]{3,})");
        private List<ImageSpan> mEmoticonsToRemove = new ArrayList<ImageSpan>(10);

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count > 0) {
                int end = start + count;
                Editable message = TagEditText.this.getEditableText();
                ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

                for (ImageSpan span : list) {
                    // Get only the emoticons that are inside of the changed
                    // region.
                    int spanStart = message.getSpanStart(span);
                    int spanEnd = message.getSpanEnd(span);
                    if ((spanStart < end) && (spanEnd > start)) {
                        // Add to remove list
                        mEmoticonsToRemove.add(span);
                    }
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            Editable message = TagEditText.this.getEditableText();

            // Commit the emoticons to be removed.
            for (ImageSpan span : mEmoticonsToRemove) {
                int startI = message.getSpanStart(span);
                int endI = message.getSpanEnd(span);

                // Remove the span
                message.removeSpan(span);

                // Remove the remaining emoticon text.
                if (startI != endI) {
                    message.delete(startI, endI);
                }
            }
            mEmoticonsToRemove.clear();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (wIsUpdating) {
                wIsUpdating = false;
                return;
            }

            Matcher matcher = wTagPattern.matcher(s);

            while (matcher.find()) {
                String tagged = matcher.group(1);
                String untagged = matcher.group(2);

                if (tagged != null) {
                    DebugUtil.debug("TAGGED >>> " + tagged);
                } else if (untagged != null) {
                    DebugUtil.debug("UNTAGGED >>> " + untagged);
                    wIsUpdating = true;

                    mStartIndex = matcher.start(2);
                    mEndIndex = matcher.end(2);
                    requestUsers(untagged);
                }
            }
        }

    };
    private int mIdentifier;
    private int mStartIndex;
    private int mEndIndex;

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
        mapTag(person);
        
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

    private void mapTag(Person person) {
        int length = person.getLogin().length();

        SpannableTagBuilder spannable = new SpannableTagBuilder(this);
        spannable.addTag(++mIdentifier, mStartIndex, (mStartIndex + length) + 1, person);
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
        mPersonPositionMap = new HashMap<Person, Pair<Integer, Integer>>(10);

        addTextChangedListener(mWatcher);
    }

    private void requestUsers(String user) {
        UserSearchModel model = new UserSearchModel();
        model.setQuery(user.replaceAll("[@]", ""));
        Requester.executeAsync(model, this);
    }

}

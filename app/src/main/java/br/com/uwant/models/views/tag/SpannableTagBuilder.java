package br.com.uwant.models.views.tag;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import br.com.uwant.R;
import br.com.uwant.models.classes.Person;

public class SpannableTagBuilder extends SpannableStringBuilder {

    private static final int TEXT_SIZE_SP = 18;

    private long mIdentifier;
    private String mSpannableTag;
    private Person mPerson;
    private TextView mTextView;

    public SpannableTagBuilder(TextView editText) {
        this.mTextView = editText;
    }

    public void addTag(long identifier, int start, int end, Person person) {
        this.mIdentifier = identifier;
        this.mPerson = person;

        String tag = getTag(this.mPerson);
        this.mSpannableTag = String.format("<uwt id='%d'>%s</uwt>", identifier, tag);

        TextView textView = createTag(tag);
        BitmapDrawable bitmapDrawable = convertToDrawable(textView);
        ImageSpan imageSpan = createImageSpan(bitmapDrawable);

        append(this.mSpannableTag);
        setSpan(imageSpan, 0, length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Editable editable = this.mTextView.getEditableText();
        if (editable.length() < end) {
            String appendable = tag.substring(editable.length() - start);
            editable.append(appendable);
        }
        editable.replace(start, end, this);

        if (this.mTextView instanceof EditText) {
            EditText editText = (EditText) this.mTextView;
            editText.setSelection(this.mTextView.getSelectionEnd());
        }
    }

    public String getTag(Person person) {
        return String.format("@%s", person.getLogin());
    }

    public long getIdentifier() {
        return this.mIdentifier;
    }

    private ImageSpan createImageSpan(BitmapDrawable bitmapDrawable) {
        return new ImageSpan(bitmapDrawable);
    }

    private TextView createTag(String tag) {
        TextView textView = new TextView(this.mTextView.getContext());
        textView.setText(tag.trim());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textView.setBackgroundResource(R.drawable.uwant_list_pressed_holo_light);
        return textView;
    }

    private BitmapDrawable convertToDrawable(TextView textView) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(spec, spec);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                textView.getMeasuredWidth(),
                textView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        c.translate(-textView.getScrollX(), -textView.getScrollY());

        textView.draw(c);
        textView.setDrawingCacheEnabled(true);

        Bitmap cacheBmp = textView.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);

        textView.destroyDrawingCache();

        BitmapDrawable bitmapDrawable = new BitmapDrawable(this.mTextView.getResources(), viewBmp);
        bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());

        return bitmapDrawable;
    }

    public void addTag(String tagged, int start, int end, Person person) {
        this.mIdentifier = person.getId();
        this.mPerson = person;
        this.mSpannableTag = tagged;

        String tag = getTag(this.mPerson);
        TextView textView = createTag(tag);
        BitmapDrawable bitmapDrawable = convertToDrawable(textView);
        ImageSpan imageSpan = createImageSpan(bitmapDrawable);

        append(this.mSpannableTag);
        setSpan(imageSpan, 0, length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Editable editable = this.mTextView.getEditableText();
        if (editable.length() < end) {
            String appendable = tag.substring(editable.length() - start);
            editable.append(appendable);
        }
        editable.replace(start, end, this);
    }

}

package br.com.uwant.models.views;

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
import android.widget.TextView;

import br.com.uwant.R;
import br.com.uwant.models.classes.Person;

public class SpannableTagBuilder extends SpannableStringBuilder {

    private int mIdentifier;
    private String mSpannableTag;
    private Person mPerson;
    private final TagEditText mEditText;

    public SpannableTagBuilder(TagEditText editText) {
        this.mEditText = editText;
    }

    public void addTag(int identifier, int start, int end, Person person) {
        this.mIdentifier = identifier;
        this.mPerson = person;

        String tag = getTag(mPerson);
        this.mSpannableTag = String.format("<uwt id='%d'>%s</uwt>", identifier, tag);

        TextView textView = createTag(tag);
        BitmapDrawable bitmapDrawable = convertToDrawable(textView);
        ImageSpan imageSpan = createImageSpan(bitmapDrawable);

        append(mSpannableTag);
        setSpan(imageSpan, 0, length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Editable editable = mEditText.getText();
        if (editable.length() < end) {
            String appendable = tag.substring(editable.length() - start);
            editable.append(appendable);
        }
        editable.replace(start, end, this);

        mEditText.setSelection(mEditText.getSelectionEnd());
    }

    public String getTag(Person person) {
        return String.format("@%s", person.getLogin());
    }

    public int getIdentifier() {
        return this.mIdentifier;
    }

    private ImageSpan createImageSpan(BitmapDrawable bitmapDrawable) {
        return new ImageSpan(bitmapDrawable);
    }

    public TextView createTag(String tag) {
        TextView textView = new TextView(mEditText.getContext());
        textView.setText(tag.trim());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textView.setBackgroundResource(R.drawable.uwant_list_pressed_holo_light);
        return textView;
    }

    public BitmapDrawable convertToDrawable(TextView textView) {
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

        BitmapDrawable bitmapDrawable = new BitmapDrawable(mEditText.getResources(), viewBmp);
        bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());

        return bitmapDrawable;
    }

}

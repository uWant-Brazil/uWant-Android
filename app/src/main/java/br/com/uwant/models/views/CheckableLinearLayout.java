package br.com.uwant.models.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

import br.com.uwant.R;

/**
 * Created by felipebenezi on 23/07/14.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private boolean checked;

    public CheckableLinearLayout(Context context) {
        super(context);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChecked(boolean b) {
        this.checked = b;
        setBackgroundResource(b ? R.drawable.uwant_list_pressed_holo_light : android.R.color.transparent);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

}

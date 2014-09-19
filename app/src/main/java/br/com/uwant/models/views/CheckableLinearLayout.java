package br.com.uwant.models.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import br.com.uwant.R;

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private CheckBox checkBox;

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
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View v = getChildAt(i);
            if (v instanceof CheckBox) {
                checkBox = (CheckBox) v;
            }
        }
    }

    public boolean isChecked() {
        return checkBox != null ? checkBox.isChecked() : false;
    }

    public void setChecked(boolean checked) {
        if (checkBox != null) {
            checkBox.setChecked(checked);
        }
    }

    public void toggle() {
        if (checkBox != null) {
            checkBox.toggle();
        }
    }

}

package br.com.uwant.models.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import br.com.uwant.R;

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private boolean checked;
    private CheckBox checkBox;

    public CheckableLinearLayout(Context context) {
        super(context);
        configure();
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure();
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        configure();
    }

    private void configure() {
        this.checkBox = (CheckBox) findViewById(R.id.checkablelinearlayou_checkbox);
    }

    @Override
    public void setChecked(boolean b) {
        if (this.checkBox == null) {
            configure();
        }

        this.checked = b;
        this.checkBox.setChecked(this.checked);
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

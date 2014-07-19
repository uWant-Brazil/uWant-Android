package br.com.uwant.utils;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public abstract class KeyboardUtil {

    public static void hide(EditText editText) {
        if (editText != null && editText.hasFocus()) {
            editText.clearFocus();
            Context context = editText.getContext();
            IBinder binder = editText.getWindowToken();

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binder, 0);
        }
    }

}

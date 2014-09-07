package br.com.uwant.models.watchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Máscara utilizando o pattern de data - dd/MM/yyyy
 * FIXME Temos que ver como iremos fazer para criar a máscara para todos os padrões que teremos suporte nas linguagens
 */
public class DateWatcher implements TextWatcher {

    private boolean mIsUpdating;
    private EditText mTextView;

    public DateWatcher(EditText textView) {
        this.mTextView = textView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing...
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mIsUpdating) {
            mIsUpdating = false;
            return;
        }
        mIsUpdating = true;

        String date = s.toString().replaceAll("[/]", "");
        if (before <= start) {
            if (s.length() <= 10) {
                char[] chars = date.toCharArray();
                StringBuilder builder = new StringBuilder();
                for (int i = 0;i < chars.length;i++) {
                    if (i == 2 || i == 4) {
                        builder.append("/");
                    }

                    int parameter;
                    switch (i) {
                        case 0:
                        case 2:
                        case 4:
                            parameter = 1;
                            break;

                        default:
                            parameter = 9;
                            break;
                    }

                    int value = Integer.valueOf(String.valueOf(chars[i]));
                    if (value <= parameter) {
                        builder.append(chars[i]);
                    } else {
                        this.mTextView.setText(date.substring(0, date.length() - 1));
                    }
                }
                date = builder.toString();
            }
        }
        this.mTextView.setText(date);
        this.mTextView.setSelection(this.mTextView.length());
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Do nothing...
    }

}

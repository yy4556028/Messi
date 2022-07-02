package com.yuyang.messi.editfilter;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

public class MoneyInputTextWatcher implements TextWatcher {

    private final EditText editText;

    public static void bindEdit(EditText editText) {
        new MoneyInputTextWatcher(editText);
    }

    private MoneyInputTextWatcher(EditText editText) {
        this.editText = editText;
        this.editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        this.editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (charSequence.toString().contains(".") && charSequence.toString().indexOf(".") < charSequence.toString().length() - 3) {
            charSequence = charSequence.subSequence(0, charSequence.toString().indexOf(".") + 3);
            editText.setText(charSequence);
            editText.setSelection(Math.min(start + count, charSequence.length()));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}

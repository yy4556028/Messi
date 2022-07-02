package com.yuyang.messi.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class ExamineTextWatcher implements TextWatcher {

    public enum ExamineType{
        ACCOUNT,
        MONEY,
    }

    private EditText editText;

    private ExamineType type;

    private CharSequence beforeText;

    public ExamineTextWatcher(EditText editText, ExamineType type) {
        this.editText = editText;
        this.type = type;
    }

    /**
     * 原文本s中，从start开始的count个字符，被长度为after的新文本所替换
     * @param s 原文本
     * @param start 原文本改变的起始位置
     * @param count 被替换掉文本的长度
     * @param after 新文本长度
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeText = s;
    }

    /**
     * 在新文本s中，从start开始的count个字符, 刚刚替换了长度为before的旧文本
     * @param s 新文本
     * @param start 新文本的起始位置
     * @param before 旧文本长度
     * @param count 新文本长度
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //输入后的文本
        CharSequence afterText = s;
    }

    /**
     * 新文本
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
    }

}

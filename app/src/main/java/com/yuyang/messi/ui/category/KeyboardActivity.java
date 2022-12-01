package com.yuyang.messi.ui.category;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.yamap.lib_keyboard.KeyboardTouchListener;
import com.yamap.lib_keyboard.ChatKeyboardUtil;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class KeyboardActivity extends AppBaseActivity {

    private LinearLayout rootView;
    private ScrollView scrollView;
    private EditText normalEdit;
    private EditText specialEdit;

    private ChatKeyboardUtil keyboardUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_keyboard;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initMoveKeyBoard();
    }

    private void initViews() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getTitle() == null ? "Keyboard" : getTitle().toString());

        rootView = findViewById(R.id.activity_keyboard_root);
        scrollView = findViewById(R.id.activity_keyboard_scrollView);
        normalEdit = findViewById(R.id.activity_keyboard_normal_edit);
        specialEdit = findViewById(R.id.activity_keyboard_special_edit);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (keyboardUtil.isShow) {
                keyboardUtil.hideSystemKeyBoard();
                keyboardUtil.hideAllKeyBoard();
                keyboardUtil.hideKeyboardLayout();
            } else {
                return super.onKeyDown(keyCode, event);
            }

            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initMoveKeyBoard() {
        keyboardUtil = new ChatKeyboardUtil(this, rootView, scrollView);
        keyboardUtil.setOtherEditText(normalEdit);
        // monitor the KeyBarod state
        keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());
        // monitor the finish or next Key
        keyboardUtil.setInputOverListener(new inputOverListener());
        specialEdit.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, ChatKeyboardUtil.INPUTTYPE_ABC, -1));
    }

    private static class KeyBoardStateListener implements ChatKeyboardUtil.KeyBoardStateChangeListener {

        @Override
        public void KeyBoardStateChange(int state, EditText editText) {
//            System.out.println("state" + state);
//            System.out.println("editText" + editText.getText().toString());
        }
    }

    private static class inputOverListener implements ChatKeyboardUtil.InputFinishListener {

        @Override
        public void inputHasOver(int onclickType, EditText editText) {
//            System.out.println("onclickType" + onclickType);
//            System.out.println("editText" + editText.getText().toString());
        }
    }
}

package com.yamap.lib_chat.keyboard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yamap.lib_chat.keyboard.utils.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

public class ExpandLayout extends LinearLayout {

    public final int DEF_KEY = Integer.MIN_VALUE;

    private final SparseArray<View> mFuncViewArrayMap = new SparseArray<>();

    private int mCurrentFuncKey = DEF_KEY;

    protected int mHeight = 0;

    public ExpandLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void addExpendView(int key, View view) {
        if (mFuncViewArrayMap.get(key) != null) {
            return;
        }
        mFuncViewArrayMap.put(key, view);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, params);
        view.setVisibility(GONE);
    }

    public void hideAllFuncView() {
        for (int i = 0; i < mFuncViewArrayMap.size(); i++) {
            int keyTemp = mFuncViewArrayMap.keyAt(i);
            mFuncViewArrayMap.get(keyTemp).setVisibility(GONE);
        }
        mCurrentFuncKey = DEF_KEY;
        setVisibility(false);
    }

    public void toggleFuncView(int key, boolean isSoftKeyboardPop, EditText editText) {
        if (getCurrentFuncKey() == key) {
            if (isSoftKeyboardPop) {
//                if (KeyboardUtil.isFullScreen((Activity) getContext())) {
//                    KeyboardUtil.closeSoftKeyboard(editText);
//                } else {
//                    KeyboardUtil.closeSoftKeyboard(getContext());
//                }
                KeyboardUtil.showHideIme(this, false);
            } else {
                KeyboardUtil.showHideIme(this, true);
            }
        } else {
            if (isSoftKeyboardPop) {
                //                if (KeyboardUtil.isFullScreen((Activity) getContext())) {
//                    KeyboardUtil.closeSoftKeyboard(editText);
//                } else {
//                    KeyboardUtil.closeSoftKeyboard(getContext());
//                }
                KeyboardUtil.showHideIme(this, false);
            }
            showFuncView(key);
        }
    }

    public void showFuncView(int key) {
        if (mFuncViewArrayMap.get(key) == null) {
            return;
        }
        for (int i = 0; i < mFuncViewArrayMap.size(); i++) {
            int keyTemp = mFuncViewArrayMap.keyAt(i);
            if (keyTemp == key) {
                mFuncViewArrayMap.get(keyTemp).setVisibility(VISIBLE);
            } else {
                mFuncViewArrayMap.get(keyTemp).setVisibility(GONE);
            }
        }
        mCurrentFuncKey = key;
        setVisibility(true);

        if (onFuncChangeListener != null) {
            onFuncChangeListener.onFuncChange(mCurrentFuncKey);
        }
    }

    public int getCurrentFuncKey() {
        return mCurrentFuncKey;
    }

    public void updateHeight(int height) {
        this.mHeight = height;
    }

    public void setVisibility(boolean b) {
        LayoutParams params = (LayoutParams) getLayoutParams();
        if (b) {
            setVisibility(VISIBLE);
            params.height = mHeight;
            if (mListenerList != null) {
                for (OnFuncKeyBoardListener l : mListenerList) {
                    l.OnFuncPop(mHeight);
                }
            }
        } else {
            setVisibility(GONE);
            params.height = 0;
            if (mListenerList != null) {
                for (OnFuncKeyBoardListener l : mListenerList) {
                    l.OnFuncClose();
                }
            }
        }
        setLayoutParams(params);
    }

    public boolean isOnlyShowSoftKeyboard() {
        return mCurrentFuncKey == DEF_KEY;
    }

    private List<OnFuncKeyBoardListener> mListenerList;

    public void addOnKeyBoardListener(OnFuncKeyBoardListener l) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        mListenerList.add(l);
    }

    public interface OnFuncKeyBoardListener {
        /**
         * 功能布局弹起
         */
        void OnFuncPop(int height);

        /**
         * 功能布局关闭
         */
        void OnFuncClose();
    }

    private OnFuncChangeListener onFuncChangeListener;

    public interface OnFuncChangeListener {
        void onFuncChange(int key);
    }

    public void setOnFuncChangeListener(OnFuncChangeListener listener) {
        this.onFuncChangeListener = listener;
    }
}
package com.yamap.lib_chat.keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yamap.lib_chat.R;
import com.yamap.lib_chat.keyboard.adpater.PageSetAdapter;
import com.yamap.lib_chat.keyboard.data.PageSetEntity;
import com.yamap.lib_chat.keyboard.utils.KeyboardUtil;
import com.yamap.lib_chat.keyboard.widget.AutoHeightLayout;
import com.yamap.lib_chat.keyboard.widget.ChatEditText;
import com.yamap.lib_chat.keyboard.widget.EmoticonViewPager;
import com.yamap.lib_chat.keyboard.widget.EmoticonsIndicator;
import com.yamap.lib_chat.keyboard.widget.EmoticonsToolBar;
import com.yamap.lib_chat.keyboard.widget.ExpandLayout;
import com.yamap.lib_chat.widget.RecordButton;

import java.util.ArrayList;

public class ChatKeyBoard extends AutoHeightLayout {

    public static final int TYPE_EMOTION = -1;
    public static final int TYPE_FUNC = -2;

    protected LayoutInflater mInflater;

    protected ImageView voiceOrTextBtn;
    protected RecordButton recordButton;
    protected ChatEditText chatEditText;
    protected ImageView emotionBtn;
    protected RelativeLayout inputLyt;
    protected ImageView expandBtn;
    protected Button sendBtn;
    protected ExpandLayout expandLyt;

    protected EmoticonViewPager emoticonsView;
    protected EmoticonsIndicator emoticonsIndicator;
    protected EmoticonsToolBar emoticonsToolBar;

    protected boolean mDispatchKeyEventPreImeLock = false;

    public ChatKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.chat_keyboard, this);
        initView();
        initFuncView();
    }

    protected void initView() {
        voiceOrTextBtn = ((ImageView) findViewById(R.id.chat_keyboard_voice_or_text_btn));
        recordButton = ((RecordButton) findViewById(R.id.chat_keyboard_voice_btn));
        chatEditText = (ChatEditText) findViewById(R.id.chat_keyboard_input_edit);
        emotionBtn = ((ImageView) findViewById(R.id.chat_keyboard_emotion_btn));
        inputLyt = ((RelativeLayout) findViewById(R.id.chat_keyboard_input_lyt));
        expandBtn = ((ImageView) findViewById(R.id.chat_keyboard_expand_btn));
        sendBtn = ((Button) findViewById(R.id.chat_keyboard_send_btn));
        expandLyt = ((ExpandLayout) findViewById(R.id.chat_keyboard_expand));

        voiceOrTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputLyt.isShown()) {
                    voiceOrTextBtn.setImageResource(R.drawable.btn_voice_or_text_keyboard);
                    showVoice();
                } else {
                    showText();
                    voiceOrTextBtn.setImageResource(R.drawable.btn_voice_or_text_voice);
                    KeyboardUtil.showHideIme(ChatKeyBoard.this, true);
                }
            }
        });
        emotionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleView(TYPE_EMOTION);
            }
        });
        expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleView(TYPE_FUNC);
            }
        });
        chatEditText.setOnBackKeyClickListener(new ChatEditText.OnBackKeyClickListener() {
            @Override
            public void onBackKeyClick() {
                if (expandLyt.isShown()) {
                    mDispatchKeyEventPreImeLock = true;
                    reset();
                }
            }
        });
    }

    protected void initFuncView() {
        initEmoticonView();
        initEditView();
    }

    protected void initEmoticonView() {
        View emotionView = mInflater.inflate(R.layout.chat_keyboard_emotion, null);
        expandLyt.addExpendView(TYPE_EMOTION, emotionView);
        emoticonsView = ((EmoticonViewPager) findViewById(R.id.chat_keyboard_emotion_emotionView));
        emoticonsIndicator = ((EmoticonsIndicator) findViewById(R.id.chat_keyboard_emotion_indicator));
        emoticonsToolBar = ((EmoticonsToolBar) findViewById(R.id.chat_keyboard_emotion_toolBar));
        emoticonsView.setOnIndicatorListener(new EmoticonViewPager.OnEmoticonsPageViewListener() {
            @Override
            public void emoticonSetChanged(PageSetEntity pageSetEntity) {
                emoticonsToolBar.setToolBtnSelect(pageSetEntity.getUuid());
            }

            @Override
            public void playTo(int position, PageSetEntity pageSetEntity) {
                emoticonsIndicator.playTo(position, pageSetEntity);
            }

            @Override
            public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
                emoticonsIndicator.playBy(oldPosition, newPosition, pageSetEntity);
            }
        });
        emoticonsToolBar.setOnToolBarItemClickListener(new EmoticonsToolBar.OnToolBarItemClickListener() {
            @Override
            public void onToolBarItemClick(PageSetEntity pageSetEntity) {
                emoticonsView.setCurrentPageSet(pageSetEntity);
            }
        });
        expandLyt.setOnFuncChangeListener(new ExpandLayout.OnFuncChangeListener() {
            @Override
            public void onFuncChange(int key) {
                ChatKeyBoard.this.onExpandChange(key);
            }
        });
    }

    public void onExpandChange(int key) {
        if (TYPE_EMOTION == key) {
            emotionBtn.setImageResource(R.drawable.icon_face_pop);
        } else {
            emotionBtn.setImageResource(R.drawable.icon_face_normal);
        }
        checkVoice();
    }

    protected void initEditView() {
        chatEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!chatEditText.isFocused()) {
                    chatEditText.setFocusable(true);
                    chatEditText.setFocusableInTouchMode(true);
                }
                return false;
            }
        });

        chatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    sendBtn.setVisibility(View.VISIBLE);
                    expandBtn.setVisibility(View.GONE);
                    sendBtn.setBackgroundResource(R.drawable.btn_send_bg);
                } else {
                    expandBtn.setVisibility(View.VISIBLE);
                    sendBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setAdapter(PageSetAdapter pageSetAdapter) {
        if (pageSetAdapter != null) {
            ArrayList<PageSetEntity> pageSetEntities = pageSetAdapter.getPageSetEntityList();
            if (pageSetEntities != null) {
                for (PageSetEntity pageSetEntity : pageSetEntities) {
                    emoticonsToolBar.addToolItemView(pageSetEntity);
                }
            }
        }
        emoticonsView.setAdapter(pageSetAdapter);
    }

    public void addFuncView(View view) {
        expandLyt.addExpendView(TYPE_FUNC, view);
    }

    public void reset() {
        KeyboardUtil.showHideIme(this, false);
        expandLyt.hideAllFuncView();
        emotionBtn.setImageResource(R.drawable.icon_face_normal);
    }

    protected void showVoice() {
        inputLyt.setVisibility(View.GONE);
        recordButton.setVisibility(View.VISIBLE);
        reset();
    }

    protected void checkVoice() {
        if (recordButton.isShown()) {
            voiceOrTextBtn.setImageResource(R.drawable.btn_voice_or_text_keyboard);
        } else {
            voiceOrTextBtn.setImageResource(R.drawable.btn_voice_or_text_voice);
        }
    }

    protected void showText() {
        inputLyt.setVisibility(View.VISIBLE);
        recordButton.setVisibility(View.GONE);
    }

    protected void toggleView(int key) {
        showText();
        expandLyt.toggleFuncView(key, isSoftKeyboardPop(), chatEditText);
    }

    protected void setFuncViewHeight(int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) expandLyt.getLayoutParams();
        params.height = height;
        expandLyt.setLayoutParams(params);
    }

    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        expandLyt.updateHeight(height);
    }

    @Override
    public void OnSoftPop(int height) {
        super.OnSoftPop(height);
        expandLyt.setVisibility(true);
        onExpandChange(expandLyt.DEF_KEY);
    }

    @Override
    public void OnSoftClose() {
        super.OnSoftClose();
        if (expandLyt.isOnlyShowSoftKeyboard()) {
            reset();
        } else {
            onExpandChange(expandLyt.getCurrentFuncKey());
        }
    }

    public void addOnFuncKeyBoardListener(ExpandLayout.OnFuncKeyBoardListener l) {
        expandLyt.addOnKeyBoardListener(l);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (mDispatchKeyEventPreImeLock) {
                    mDispatchKeyEventPreImeLock = false;
                    return true;
                }
                if (expandLyt.isShown()) {
                    reset();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (KeyboardUtil.isFullScreen((Activity) getContext())) {
            return false;
        }
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (KeyboardUtil.isFullScreen((Activity) getContext())) {
            return;
        }
        super.requestChildFocus(child, focused);
    }

    public boolean dispatchKeyEventInFullScreen(KeyEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (KeyboardUtil.isFullScreen((Activity) getContext()) && expandLyt.isShown()) {
                    reset();
                    return true;
                }
            default:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    boolean isFocused;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        isFocused = chatEditText.getShowSoftInputOnFocus();
                    } else {
                        isFocused = chatEditText.isFocused();
                    }
                    if (isFocused) {
                        chatEditText.onKeyDown(event.getKeyCode(), event);
                    }
                }
                return false;
        }
    }

    public ChatEditText getEditText() {
        return chatEditText;
    }

    public RecordButton getRecordBtn() {
        return recordButton;
    }

    public Button getBtnSend() {
        return sendBtn;
    }

    public EmoticonViewPager getEmoticonsFuncView() {
        return emoticonsView;
    }

    public EmoticonsToolBar getEmoticonsToolBarView() {
        return emoticonsToolBar;
    }
}

package com.yuyang.messi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;

import java.util.Set;

public class QuickAlphabeticBar extends View {

    public static final String ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";//索引

    private TextView mDialogText; // 中间显示字母的文本框

    private Paint paint;

    private int choose = -1;

    private Drawable focusDrawable;

    private Set<String> sectionSet;

    public QuickAlphabeticBar(Context context) {
        this(context, null);
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(PixelUtils.dp2px(100));
        drawable.setColor(ContextCompat.getColor(context, R.color.gray_));
        focusDrawable = drawable;

        paint = new Paint();
        paint.setTextSize(20);
        paint.setAntiAlias(true);
    }

    public void setTextView(TextView mTextDialog) {
        mDialogText = mTextDialog;
    }

    public void setSectionSet(Set<String> sectionSet) {
        this.sectionSet = sectionSet;
        invalidate();
    }

    public void updateSection(int section) {
        int position = ALPHABET.indexOf(section);
        if (position != choose) {
            choose = position;
            invalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float y = event.getY();
        final int oldChoose = choose;
        // 计算手指位置，找到对应的段，让mList移动段开头的位置上
        int selectIndex = (int) (y / (getHeight() / ALPHABET.length()));

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                setBackgroundColor(Color.TRANSPARENT);
                choose = -1;
                invalidate();
                if (mDialogText != null) {
                    mDialogText.setVisibility(View.INVISIBLE);
                }
                break;
            }
            default: {
                setBackground(focusDrawable);
                if (oldChoose != selectIndex) {
                    if (selectIndex >= 0 && selectIndex < ALPHABET.length()) {
                        if (onChangeListener != null) {
                            onChangeListener.onChange(String.valueOf(ALPHABET.charAt(selectIndex)));
                        }
                        if (mDialogText != null) {
                            mDialogText.setText(String.valueOf(ALPHABET.charAt(selectIndex)));
                            mDialogText.setVisibility(View.VISIBLE);
                        }
                        choose = selectIndex;
                        invalidate();
                    }
                }
            }
            break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / ALPHABET.length(); // 单个字母占的高度

        for (int i = 0; i < ALPHABET.length(); i++) {

            // 选中的状态
            if (i == choose) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.theme)); // 滑动时按下字母颜色
                paint.setFakeBoldText(true);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setAlpha(255);
            } else if (sectionSet == null || sectionSet.contains(String.valueOf(ALPHABET.charAt(i)))) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
                paint.setFakeBoldText(false);
                paint.setTypeface(Typeface.DEFAULT);
                paint.setAlpha(255);
            } else {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.textHint));
                paint.setFakeBoldText(false);
                paint.setTypeface(Typeface.DEFAULT);
                paint.setAlpha(100);
            }

            // 绘画的位置
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2f - paint.measureText(ALPHABET.charAt(i) + "") / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(ALPHABET.charAt(i) + "", xPos, yPos, paint);
        }
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public interface OnChangeListener {
        void onChange(String alphabet);
    }

    private OnChangeListener onChangeListener;

}

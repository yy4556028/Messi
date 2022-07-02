package com.yuyang.messi.view;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 使用setMovementMethod才能使TextView里面的元素自动拥有点击功能，支持ClickSpan。
 * 但是加上这个方法会造成ListView的每个项无的文本会占用ListView的ItemClick,解决这个办法需要重写一个setMovementMethod方法
 * {@link TextViewFixTouchConsume}
 */
public class MyLinkMovementMethod extends LinkMovementMethod {

    private static final int MINIFY_AREA_FOR_CLICK = 10;

    private static MyLinkMovementMethod sInstance;
    private boolean noActionDown = true;
    int dragStart_x = 0;
    int dragStart_y = 0;
    int dragEnd_x = 0;
    int dragEnd_y = 0;

    public static MovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new MyLinkMovementMethod();

        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            if (action == MotionEvent.ACTION_DOWN) {
                dragStart_x = (int) event.getRawX();
                dragStart_y = (int) event.getRawY();
                //This flag add for htc328 because of touch down event missing.
                noActionDown = false;
            }

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    dragEnd_x = (int) event.getRawX();
                    dragEnd_y = (int) event.getRawY();

                    int dx = dragEnd_x - dragStart_x;
                    int dy = dragEnd_y - dragStart_y;

                    if (((dx > MINIFY_AREA_FOR_CLICK ||
                            dy > MINIFY_AREA_FOR_CLICK
                            || dx < -MINIFY_AREA_FOR_CLICK ||
                            dy < -MINIFY_AREA_FOR_CLICK)) && !noActionDown) {
                        Selection.removeSelection(buffer);
                        return true;
                    }

                    link[0].onClick(widget);
                } else {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }
                return true;
            } else {
                Selection.removeSelection(buffer);
                Touch.onTouchEvent(widget, buffer, event);
                return false;
            }
        }
        return Touch.onTouchEvent(widget, buffer, event);//super.onTouchEvent(widget, buffer, event);
    }
}

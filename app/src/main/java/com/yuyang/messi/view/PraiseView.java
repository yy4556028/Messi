package com.yuyang.messi.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.yuyang.messi.R;
import com.yuyang.messi.view.CustomShapeSquareImageView.CustomShapeImageView;
import com.yuyang.messi.view.CustomShapeSquareImageView.CustomShapeSquareImageView;

import java.util.Random;

public class PraiseView extends RelativeLayout {

    private int count;

    private int[] colors = new int[]{R.color.red, R.color.blue, R.color.yellow, R.color.purple, R.color.gray, R.color.cyan};

    public PraiseView(Context context) {
        this(context, null);
    }

    public PraiseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        count = 0;
    }

    private void createImage(float downX, float downY) {

        final CustomShapeSquareImageView view = new CustomShapeSquareImageView(getContext(), 0, colors[new Random().nextInt(colors.length)], CustomShapeImageView.Shape.SVG, R.raw.shape_heart);
        int size = new Random().nextInt(50) + 100;
        LayoutParams params = new LayoutParams(size, size);
        params.setMargins((int) downX, (int) downY, 0, 0);
        view.setLayoutParams(params);
        addView(view);

        ObjectAnimator zoomInX = ObjectAnimator.ofFloat(view, "scaleX", 0, 1.3f);
        ObjectAnimator zoomInY = ObjectAnimator.ofFloat(view, "scaleY", 0, 1.3f);
        ObjectAnimator zoomOutX = ObjectAnimator.ofFloat(view, "scaleX", 1.3f, 1);
        ObjectAnimator zoomOutY = ObjectAnimator.ofFloat(view, "scaleY", 1.3f, 1);

        ObjectAnimator transY = ObjectAnimator.ofFloat(view, "translationY", 0, -1000);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0, new Random().nextInt(90) - 45);

        zoomInX.setDuration(200);
        zoomInY.setDuration(200);
        zoomOutX.setDuration(100);
        zoomOutY.setDuration(100);

        transY.setDuration(5000);
        alpha.setDuration(5000);
        rotate.setDuration(5000);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(zoomInX).with(zoomInY);
        animSet.play(zoomOutX).after(zoomInY);
        animSet.play(zoomOutX).with(zoomOutY);
        animSet.play(transY).after(zoomOutX);
        animSet.play(alpha).with(transY);
        animSet.play(rotate).with(alpha);
        animSet.setInterpolator(new AccelerateInterpolator());
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                removeView(view);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animSet.start();
    }

    public int getPraiseCount() {
        return count;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                createImage(event.getX(), event.getY());
                count++;

                if (listener != null) {
                    listener.onPraise(count);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private onPraiseListener listener;

    public interface onPraiseListener {
        void onPraise(int count);
    }

    public void setOnPraiseListener(onPraiseListener listener) {
        this.listener = listener;
    }
}

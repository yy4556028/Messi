package com.yuyang.messi.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.yuyang.messi.R;
import com.yuyang.messi.bean.DBFreeFeed;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.ToastUtil;

public class VibingCard extends RelativeLayout {

    public static final int DRAG_DIRECTION_CENTER = 0;
    public static final int DRAG_DIRECTION_LEFT = 1;
    public static final int DRAG_DIRECTION_RIGHT = 2;
    public static final int DRAG_DIRECTION_DOWN = 3;

    private float posX;
    private float posY;
    private float LIMIT_DISTANCE = 5;
    private int startX = 0;
    private int startY = 0;
    private int currentX = 0;
    private int currentY = 0;

    private MediaPlayer mediaPlayer;

    private DBFreeFeed currentFeed;

    public VibingCard(Context context) {
        this(context, null);
    }

    public VibingCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VibingCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.vibing_card, this);
        findViewById(R.id.vibing_card_avatar).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast("click");
                cardFlashOutInDirection(DRAG_DIRECTION_LEFT);
            }
        });
    }

    public void init(final float posX, final float posY) {
        this.posX = posX;
        this.posY = posY;

        setX(posX);
        setY(-CommonUtil.getScreenHeight());
        cardFlashOutInDirection(DRAG_DIRECTION_CENTER);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = (int) event.getRawX();
        currentY = (int) event.getRawY();

        int xDistance = currentX - startX;
        int yDistance = currentY - startY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = currentX;
                startY = currentY;
                break;
            case MotionEvent.ACTION_MOVE:
                setRotation((float) (xDistance * Math.PI / 100));
                setX(posX + xDistance);
                setY(posY + yDistance);
                break;
            case MotionEvent.ACTION_UP:
                if ((yDistance - CommonUtil.getScreenHeight() / 6.0f) / 200.0f >= 0.9) {
                    cardFlashOutInDirection(DRAG_DIRECTION_DOWN);
                } else if (xDistance > CommonUtil.getScreenWidth() / 6) {
                    cardFlashOutInDirection(DRAG_DIRECTION_RIGHT);
                } else if (xDistance < -CommonUtil.getScreenWidth() / 6) {
                    cardFlashOutInDirection(DRAG_DIRECTION_LEFT);
                } else {
                    cardFlashOutInDirection(DRAG_DIRECTION_CENTER);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//                startX = (int) ev.getX(ev.getActionIndex());    //获取第一个触点的 X 坐标
//                startY = (int) ev.getY(ev.getActionIndex());    //获取第一个触点的 Y 坐标
                startX = (int) ev.getRawX();
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:           //除第一个触点外，其他触点被按下时该方法被触发。
//                startX_2 = ev.getX(ev.getActionIndex());    //获取第二个触点的坐标
//                startY_2 = ev.getY(ev.getActionIndex());    //获取第二个触点的坐标
                break;
            case MotionEvent.ACTION_MOVE:

                float xDistance = ev.getRawX() - startX;
                float yDistance = ev.getRawY() - startY;

                setX(posX + xDistance);
                setY(posY + yDistance);

                if (Math.abs(xDistance) > LIMIT_DISTANCE && Math.abs(yDistance) > LIMIT_DISTANCE) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startX = 0;
                startY = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:             //除第一个触点外，其他触点弹起时该方法被触发。
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void cardFlashOutInDirection(final int direction) {

        ObjectAnimator rotation = ObjectAnimator.ofFloat(this, "rotation", 0.0f);
        float x = posX;
        float y = posY;

        switch (direction) {
            case DRAG_DIRECTION_CENTER:
                break;
            case DRAG_DIRECTION_LEFT:
                x = -CommonUtil.getScreenWidth();
                break;
            case DRAG_DIRECTION_RIGHT:
                x = CommonUtil.getScreenWidth();
                break;
            case DRAG_DIRECTION_DOWN:
                y = CommonUtil.getScreenHeight();
                break;
        }

        ObjectAnimator translateX = ObjectAnimator.ofFloat(this, "x", x);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(this, "y", y);

        AnimatorSet set = new AnimatorSet();
        set.play(rotation).with(translateX).with(translateY);

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                    mediaPlayer.stop();
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                switch (direction) {

                    case DRAG_DIRECTION_CENTER:
                        if (listener != null) {
                            listener.onCenter();
                        }
                        break;

                    case DRAG_DIRECTION_LEFT:
                        if (listener != null) {
                            listener.onDragLeft();
                        }
                        setX(posX);
                        setY(-CommonUtil.getScreenHeight());
                        setBackgroundColor(Color.BLUE);
                        cardFlashOutInDirection(DRAG_DIRECTION_CENTER);
                        break;
                    case DRAG_DIRECTION_RIGHT:
                        if (listener != null) {
                            listener.onDragRight();
                        }
                        setX(posX);
                        setY(-CommonUtil.getScreenHeight());
                        setBackgroundColor(Color.YELLOW);
                        cardFlashOutInDirection(DRAG_DIRECTION_CENTER);
                        break;
                    case DRAG_DIRECTION_DOWN:
                        if (listener != null) {
                            listener.onDragDown();
                        }
                        setX(posX);
                        setY(-CommonUtil.getScreenHeight());
                        setBackgroundColor(Color.CYAN);
                        cardFlashOutInDirection(DRAG_DIRECTION_CENTER);
                        break;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        set.start();
    }

//    public void fillWithFeed(final DBFreeFeed feed) {
//
//        post(new Runnable() {
//            @Override
//            public void run() {
//                if (feed != null) {
//                        if (feed.isSentByMe()) {
//                            cheersOverlay.setVisibility(View.INVISIBLE);
//                            passOverlay.setVisibility(View.INVISIBLE);
//                            reportOrDeleteOverlay.setImageDrawable(SobrrConstants.getSobrrDrawerable(R.drawable.vibing_delete_overlay));
//                        } else {
//                            cheersOverlay.setVisibility(View.VISIBLE);
//                            passOverlay.setVisibility(View.VISIBLE);
//                            reportOrDeleteOverlay.setImageDrawable(SobrrConstants.getSobrrDrawerable(R.drawable.vibing_report_overlay));
//                        }
//                    currentFeed = feed;
//                    UserBean member = feed.getUserBean();
//                    currentFeedId = f.getRemote_id();
//                    currentMemberId = member.getRemote_id();
//                    currentFeedImageUrl = f.getPicture();
//                    currentFeedBody = f.getBody();
//                    currentFeedWebUrl = f.getWeb_url();
//
//                    if (showingType != VibingCardShowingType.RECENT_VIBING) {
//                        SobrrActionUtil.getGlideInstance().load(member.getBackground_url_qiniu()).fetch();
//                    }
//
//                    fillAvatarView(f);
//                    fillImageView(f);
//                    fillFeedBody(f);
//                    fillTimeLeft(f);
//                    fillButtonRowLayout(f);
//
//                    relocationBottomTextViews();
//                }
//            }
//        });
//    }

    public interface VibingCardListener {
        void onDragLeft();

        void onDragRight();

        void onDragDown();

        void onCenter();
    }

    private VibingCardListener listener;

    public void setVibingCardListener(VibingCardListener listener) {
        this.listener = listener;
    }
}

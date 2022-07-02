package com.yuyang.messi.ui.vibe;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.DBFreeFeed;
import com.yuyang.messi.controll.FeedController;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.messi.widget.VibingCard;

import java.lang.ref.WeakReference;

public class VibeFragment extends BaseFragment {

    private static final String TAG = VibeFragment.class.getSimpleName();

    private static final int VIBE_WIDTH_HEIGHT_DIFF = PixelUtils.dp2px(45f);

    private static final int VIBE_WIDTH = (int) (CommonUtil.getScreenWidth() * 0.94f);
    private static final int VIBE_HEIGHT = VIBE_WIDTH + VIBE_WIDTH_HEIGHT_DIFF;

    private RelativeLayout rootLayout;
    private ProgressBar progressBar;

    private VibingCard vibingCard;

    private FeedController controller;
    private UiHandler mHandler = new UiHandler(this);

    @Override
    public int getLayoutId() {
        return R.layout.fragment_vibe;
    }

    @Override
    public void doOnViewCreated() {

        setRetainInstance(true);

        controller = FeedController.getInstance();
        controller.setHandler(mHandler);
        controller.clearFeeds();

        findViewById(getContentView());
        initVibingCard();
    }

    private void findViewById(View view) {
        rootLayout = (RelativeLayout) view.findViewById(R.id.fragment_vibe_root_lyt);
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_vibe_progress);
    }

    private void initVibingCard() {
        if (vibingCard == null) {
            vibingCard = new VibingCard(getActivity());
            vibingCard.setBackgroundColor(Color.RED);
            rootLayout.bringChildToFront(progressBar);
            vibingCard.setX((CommonUtil.getScreenWidth() - VIBE_WIDTH) / 2.0f);
            vibingCard.setY(-CommonUtil.getScreenHeight());

            vibingCard.setVibingCardListener(new VibingCard.VibingCardListener() {
                @Override
                public void onDragLeft() {

                }

                @Override
                public void onDragRight() {

                }

                @Override
                public void onDragDown() {

                }

                @Override
                public void onCenter() {

                }
            });

        }
        rootLayout.addView(vibingCard, new RelativeLayout.LayoutParams(VIBE_WIDTH, VIBE_HEIGHT));

        rootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                float vibingY = (rootLayout.getHeight() - VIBE_HEIGHT) * 0.45f;
                if (vibingY > 0) {
                    vibingCard.init((CommonUtil.getScreenWidth() - VIBE_WIDTH) / 2.0f, vibingY);
                }
            }
        }, 1000);

        DBFreeFeed freeFeed = controller.getNextFeed();
        if (freeFeed != null) {
//            vibingCard.fillWithFeed(freeFeed);
            vibingCard.cardFlashOutInDirection(vibingCard.DRAG_DIRECTION_CENTER);
        }
    }

    public static class UiHandler extends Handler {
        WeakReference<Fragment> weakReference;

        public UiHandler(Fragment activity) {
            weakReference = new WeakReference<Fragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            VibeFragment fragment = (VibeFragment) weakReference.get();
            switch (msg.what) {
                case FeedController.MSG_START_FEED_LOADING: {
//                    fragment.startLoading();
                    break;
                }
                case FeedController.MSG_FINISH_FEED_LOADING: {
//                    fragment.stopLoading();
//                    fragment.loadVibing();
                    break;
                }
            }
        }
    }

}

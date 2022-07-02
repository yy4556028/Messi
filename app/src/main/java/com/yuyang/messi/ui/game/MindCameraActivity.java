package com.yuyang.messi.ui.game;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class MindCameraActivity extends AppBaseActivity {

    private MindCameraView mindCameraView;
    private TextView tipText;
    private int status = 0;//0 初始 -1 已结束 1 已开始

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mind_camera;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("MindCamera");

        mindCameraView = findViewById(R.id.activity_mind_camera_mindCameraView);
        tipText = findViewById(R.id.activity_mind_camera_tipText);

        mindCameraView.setOnMindClickListener(new MindCameraView.OnMindClickListener() {
            @Override
            public void onClickIndex(int index) {
                if (index == -1) return;
                if (index == mindCameraView.getDotViewList().size() - 1) {
                    rightToCenter(false, "Level " + (index + 2));
                } else {
                    status = -1;
                    rightToCenter(true, "Level " + mindCameraView.getDotViewList().size() + "\r\n" + "Game Over");
                }
            }
        });
        tipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case 0: {
                        status = 1;
                        mindCameraView.addNewCircle();
                        centerToLeft();
                        break;
                    }
                    case -1: {
                        status = 0;
                        mindCameraView.getDotViewList().clear();
                        tipText.setText("Level 1\r\nClick To Start");
                        break;
                    }
                    case 1: {
                        break;
                    }
                }

            }
        });

        tipText.setText("Level 1\r\nClick To Start");
    }

    private void centerToLeft() {
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(tipText, "translationX", 0, -tipText.getWidth());
        translationAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mindCameraView.setCanTouch(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translationAnim.start();
    }

    private void rightToCenter(final boolean isOver, String text) {
        tipText.setText(text);
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(tipText, "translationX", tipText.getWidth(), 0);
        translationAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mindCameraView.setCanTouch(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isOver) {
                    mindCameraView.addNewCircle();

                    tipText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            centerToLeft();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translationAnim.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mind_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_hint) {
            mindCameraView.tip();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

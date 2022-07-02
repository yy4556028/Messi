package com.yuyang.messi.ui.autoclick;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.helper.AutoClickHelper;
import com.yuyang.messi.threadPool.ThreadPool;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.Random;

public class AutoClickActivity extends AppBaseActivity {

    private EditText etClickX;
    private EditText etClickY;
    private TextView tvStart;

    private final Random mRandom = new Random();

    private float clickX, clickY;
    private final AutoClickHelper autoClickHelper = new AutoClickHelper();

    private boolean isClicking = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auto_click;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClicking = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e(AutoClickActivity.class.getSimpleName(), ev.getRawX() + "-" + ev.getRawY());
        return super.dispatchTouchEvent(ev);
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("自动点击");

        etClickX = findViewById(R.id.activity_auto_click_etClickX);
        etClickY = findViewById(R.id.activity_auto_click_etClickY);
        tvStart = findViewById(R.id.activity_auto_click_tvStart);

        etClickX.setText(String.valueOf(900));
        etClickY.setText(String.valueOf(2100));
    }

    private void initEvent() {

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    clickX = Float.parseFloat(etClickX.getText().toString());
                    clickY = Float.parseFloat(etClickY.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast("输入坐标有误");
                    return;
                }
                startAutoClick();
            }
        });
    }

    private void startAutoClick() {
        isClicking = true;
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (isClicking) {
                    try {
                        Thread.sleep(500 + mRandom.nextInt(15));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ClickBean clickBean = createBean();
                    autoClickHelper.autoClickPos(clickBean.startX, clickBean.startY, clickBean.endX, clickBean.endY);
                }
            }
        });
    }

    private ClickBean createBean() {
        ClickBean clickBean = new ClickBean(
                clickX + mRandom.nextInt(5) - 10,
                clickY + mRandom.nextInt(5) - 10,
                clickX + +mRandom.nextInt(5) - 10,
                clickY + +mRandom.nextInt(5) - 10);
        return clickBean;
    }

    private static class ClickBean {

        private float startX;
        private float startY;
        private float endX;
        private float endY;

        public ClickBean(float startX, float startY, float endX, float endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        public float getStartX() {
            return startX;
        }

        public void setStartX(float startX) {
            this.startX = startX;
        }

        public float getStartY() {
            return startY;
        }

        public void setStartY(float startY) {
            this.startY = startY;
        }

        public float getEndX() {
            return endX;
        }

        public void setEndX(float endX) {
            this.endX = endX;
        }

        public float getEndY() {
            return endY;
        }

        public void setEndY(float endY) {
            this.endY = endY;
        }
    }
}

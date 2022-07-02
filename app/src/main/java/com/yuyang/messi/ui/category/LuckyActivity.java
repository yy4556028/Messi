package com.yuyang.messi.ui.category;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.view.LuckyWheelView;
import com.yuyang.messi.view.ScratchImageView;
import com.yuyang.messi.view.ScratchTextView;

public class LuckyActivity extends AppBaseActivity {

    private EditText luckyIndexEdit;
    private LuckyWheelView wheelView;
    private ImageView mStartBtn;

    private ScratchImageView scratchImageView;
    private ScratchTextView scratchTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lucky;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initEvent();

        setTitle(null);
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Lucky");

        luckyIndexEdit = findViewById(R.id.activity_lucky_index);
        luckyIndexEdit.setHint("中将索引");
        wheelView = findViewById(R.id.activity_lucky_wheel);
        mStartBtn = findViewById(R.id.activity_lucky_start);

        scratchImageView = findViewById(R.id.activity_lucky_scratch_image);
        scratchTextView = findViewById(R.id.activity_lucky_scratch_text);
        scratchTextView.setText("hehe");
    }

    private void initEvent() {
        mStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = luckyIndexEdit.getText().toString();
                int luckyIndex;

                if (TextUtils.isEmpty(str)) {
                    luckyIndex = -1;
                } else {
                    luckyIndex = Integer.parseInt(str);
                }

                if (!wheelView.isStart()) {
                    mStartBtn.setImageResource(R.drawable.lucky_wheel_stop);
                    wheelView.luckyStart(luckyIndex);
                } else {
                    if (wheelView.getState() != LuckyWheelView.State.END) {
                        mStartBtn.setImageResource(R.drawable.lucky_wheel_start);
                        wheelView.luckyEnd();
                    }
                }
            }
        });

        wheelView.setOnEndListener(new LuckyWheelView.OnEndListener() {
            @Override
            public void onEnd(int index) {
                ToastUtil.showToast(index + "");
            }
        });

        scratchImageView.setRevealListener(new ScratchImageView.IRevealListener() {
            @Override
            public void onRevealed(ScratchImageView tv) {
                ToastUtil.showToast("Finish");
            }
        });

        scratchTextView.setRevealListener(new ScratchTextView.IRevealListener() {
            @Override
            public void onRevealed(ScratchTextView tv) {
                ToastUtil.showToast(scratchTextView.getText().toString());
            }
        });
    }

}

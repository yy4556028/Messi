package com.yuyang.messi.ui.category;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * 角标
 */
public class BadgeActivity extends AppBaseActivity {

    private EditText numEdit;

    private Button setBadgeBtn;
    private Button removeBadgeBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_badge;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvents();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("角标");

        numEdit = findViewById(R.id.activity_badge_numEdit);
        setBadgeBtn = findViewById(R.id.activity_badge_setBadgeBtn);
        removeBadgeBtn = findViewById(R.id.activity_badge_removeBadgeBtn);

        TextView tvBadge1 = findViewById(R.id.tvBadge1);
        tvBadge1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BadgeDrawable badgeDrawable1 = BadgeDrawable.create(getActivity());
                badgeDrawable1.setBadgeGravity(BadgeDrawable.TOP_START);
                badgeDrawable1.setNumber(99);
                badgeDrawable1.setBackgroundColor(Color.RED);
                badgeDrawable1.setVerticalOffset(15);
                badgeDrawable1.setHorizontalOffset(15);
                BadgeUtils.attachBadgeDrawable(badgeDrawable1, setBadgeBtn);

                BadgeDrawable badgeDrawable2 = BadgeDrawable.create(getActivity());
                badgeDrawable2.setBadgeGravity(BadgeDrawable.TOP_END);
                badgeDrawable2.setBackgroundColor(Color.RED);
                BadgeUtils.attachBadgeDrawable(badgeDrawable2, findViewById(R.id.tvBadge1));

                BadgeDrawable badgeDrawable3 = BadgeDrawable.create(getActivity());
                badgeDrawable3.setNumber(99999);
                badgeDrawable3.setMaxCharacterCount(3);
                badgeDrawable3.setBadgeGravity(BadgeDrawable.TOP_END);
                badgeDrawable3.setBackgroundColor(Color.RED);
                BadgeUtils.attachBadgeDrawable(badgeDrawable3, findViewById(R.id.ivBadge));

                tvBadge1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initEvents() {
        setBadgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(numEdit.getText().toString());
                } catch (NumberFormatException e) {
                    ToastUtil.showToast("Error input");
                    return;
                }

                boolean success = ShortcutBadger.applyCount(getActivity(), badgeCount);
                ToastUtil.showToast("Set count=" + badgeCount + ", success=" + success);
            }
        });
        removeBadgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = ShortcutBadger.removeCount(getActivity());
                ToastUtil.showToast("success=" + success);
            }
        });
    }

}


package com.yuyang.lib_base.ui.header;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.yuyang.lib_base.R;
import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class HeaderLayout extends Toolbar {

    private Toolbar toolbar;
    private ActionBar actionBar;

    private TextView leftText;
    private TextView titleView;

    private LinearLayout rightContainer, centerContainer;

    @ColorInt
    private int imageAndTextColor = -1;

    public HeaderLayout(Context context) {
        this(context, null);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_header, this);
        init((AppCompatActivity) context);
    }

    private void init(AppCompatActivity activity) {
        setBackgroundResource(R.color.white);
        toolbar = findViewById(R.id.view_header_toolbar);
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
//        actionBar.setHomeButtonEnabled(false);//决定左上角的图标是否可以点击
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setElevation(25);

        leftText = findViewById(R.id.view_header_leftText);
        titleView = findViewById(R.id.view_header_titleText);
        rightContainer = findViewById(R.id.rightContainer);
        centerContainer = findViewById(R.id.centerContainer);
        setPadding(0, MyStatusBarUtil.getStatusBarHeight(), 0, 0);
        showLeftBackButton();

        //为了标题居中
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                centerContainer.setPadding(0, 0, getWidth() - centerContainer.getRight() - centerContainer.getLeft(), 0);
            }
        });
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setImageAndTextColor(@ColorInt int color) {
        imageAndTextColor = color;
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_back);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);//此方法有问题
        DrawableCompat.setTint(DrawableCompat.wrap(drawable).mutate(), color);
        toolbar.setNavigationIcon(drawable);
        leftText.setTextColor(color);
        titleView.setTextColor(color);
        for (int i = 0; i < rightContainer.getChildCount(); i++) {
            View view = rightContainer.getChildAt(i);
            if (view instanceof HeaderRightItem) {
                HeaderRightItem rightItem = (HeaderRightItem) view;
                rightItem.imageView.setColorFilter(color);
                rightItem.textView.setTextColor(color);
            }
        }
    }

    public void showLeftBackButton() {
        showLeftBackButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputView(v);
                ActivityCompat.finishAfterTransition(((Activity) getContext()));
            }
        });
    }

    public void showLeftBackButton(OnClickListener onClickListener) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        leftText.setVisibility(GONE);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    public void hideLeftBackButton() {
        actionBar.setDisplayHomeAsUpEnabled(false);
        leftText.setVisibility(GONE);
    }

    public TextView getLeftText() {
        return leftText;
    }

    public void setLeftText(String text) {
        this.setLeftText(text, new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputView(v);
                ActivityCompat.finishAfterTransition(((Activity) getContext()));
            }
        });
    }

    public void setLeftText(String text, OnClickListener listener) {
        actionBar.setDisplayHomeAsUpEnabled(false);
        leftText.setVisibility(View.VISIBLE);
        leftText.setText(text);
        leftText.setOnClickListener(listener);
    }

    public void showTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            titleView.setVisibility(GONE);
        } else {
            titleView.setVisibility(VISIBLE);
            titleView.setText(title);
        }
    }

    public LinearLayout getRightContainer() {
        return rightContainer;
    }

    public LinearLayout getCenterContainer() {
        return centerContainer;
    }

    public List<HeaderRightItem> setRight(List<HeaderRightBean> rightBeanList) {
        toolbar.getMenu().clear();
        rightContainer.removeAllViews();
        if (rightBeanList == null || rightBeanList.size() == 0) {
            return null;
        }
        List<HeaderRightItem> rightItemList = new ArrayList<>();
        for (HeaderRightBean rightBean : rightBeanList) {
            HeaderRightItem headerRightItem = new HeaderRightItem(getContext());
            if (imageAndTextColor != -1) {
                headerRightItem.imageView.setColorFilter(imageAndTextColor);
                headerRightItem.textView.setTextColor(imageAndTextColor);
            }
            if (rightBean.getImageRes() != 0) {
                headerRightItem.setImageRes(rightBean.getImageRes(), rightBean.getImagePadding());
            } else if (!TextUtils.isEmpty(rightBean.getText())) {
                headerRightItem.setText(rightBean.getText());
            }
            headerRightItem.setOnClickListener(rightBean.getClickListener());
            rightContainer.addView(headerRightItem);
            rightItemList.add(headerRightItem);
        }
        return rightItemList;
    }

    public void setMenu(final List<HeaderRightBean> beanList, final Toolbar.OnMenuItemClickListener listener) {
        setMenu(beanList, listener, 0);
    }

    public void setMenu(final List<HeaderRightBean> beanList, final Toolbar.OnMenuItemClickListener listener, @ColorInt final int tintColor) {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                rightContainer.removeAllViews();
                toolbar.getMenu().clear();
                for (HeaderRightBean rightBean : beanList) {
                    if (rightBean.getImageRes() != 0) {
                        Drawable drawable = ContextCompat.getDrawable(getContext(), rightBean.getImageRes());
                        if (drawable != null && tintColor != 0) {
                            DrawableCompat.setTint(DrawableCompat.wrap(drawable).mutate(), tintColor);
                        } else if (drawable != null && imageAndTextColor != -1) {
                            DrawableCompat.setTint(DrawableCompat.wrap(drawable).mutate(), imageAndTextColor);
                        }
                        toolbar.getMenu().add(rightBean.getText()).setIcon(drawable).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    } else {
                        toolbar.getMenu().add(rightBean.getText()).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    }
                }
                toolbar.setOnMenuItemClickListener(listener);
            }
        });
    }

    private static void hideSoftInputView(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

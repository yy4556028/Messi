package com.yuyang.lib_base.ui.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.yuyang.lib_base.R;


public class HeaderRightItem extends RelativeLayout {

    public AppCompatImageView imageView;
    public TextView textView;

    public HeaderRightItem(Context context) {
        this(context, null);
    }

    public HeaderRightItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_header_right_item, this);
        init();
    }

    private void init() {
        imageView = findViewById(R.id.view_header_right_item_imageView);
        textView = findViewById(R.id.view_header_right_item_textView);
    }

    public void setImageRes(int imageRes, int padding) {
        imageView.setVisibility(VISIBLE);
        textView.setVisibility(GONE);
        imageView.setImageResource(imageRes);
        imageView.setPadding(padding, padding, padding, padding);
    }

    public void setText(String text) {
        imageView.setVisibility(GONE);
        textView.setVisibility(VISIBLE);
        textView.setText(text);
    }

    public String getText() {
        return textView.getText().toString();
    }
}

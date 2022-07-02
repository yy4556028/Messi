package com.yuyang.messi.ui.home;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.yuyang.lib_base.net.common.RxUtils;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.view.ClickableTagText;
import com.yuyang.messi.view.Progress.SwordLoadingDialog;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;

public class TestFragment extends BaseFragment {

    private SwordLoadingDialog swordLoadingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }

    @Override
    protected void doOnViewCreated() {
        initViews();
    }

    private void initViews() {
        TextView textView = $(R.id.fragment_test_text);
        String str = "小明和小强都是张老师的学生，\n" +
                "张老师的生日是M月N日，\n" +
                "2人都知道张老师的生日是下列10组中的一天，\n" +
                "张老师把M值告诉了小明，把N值告诉了小强，\n" +
                "张老师问他们知道他的生日是那一天吗？ \n\n" +
                "3月4日\t\t3月5日\t\t3月8日 \n" +
                "6月4日\t\t6月7日 \n" +
                "9月1日\t\t9月5日 \n" +
                "12月1日\t\t12月2日\t\t12月8日 \n\n" +
                "小明说：如果我不知道的话，小强肯定也不知道 \n" +
                "小强说：本来我也不知道，但是现在我知道了 \n" +
                "小明说：哦，那我也知道了 \n" +
                "请根据以上对话推断出张老师的生日是哪一天";

        Spannable wordToSpan = new SpannableString(str);
        Pattern p = Pattern.compile("\\d{1,2}月\\d{1,2}日");
        Matcher tagMatcher = p.matcher(str);
        while (tagMatcher.find()) {
            int start = tagMatcher.start();
            int end = tagMatcher.end();
            final String matchString = str.substring(start, end);
            ClickableTagText tagText = new ClickableTagText(Color.CYAN, true);
            tagText.setOnClickLister(new ClickableTagText.OnClickLister() {
                @Override
                public void OnClick(View widget) {
                    if (matchString.equals("9月1日")) {
                        ToastUtil.showToast("答对啦~");
                    } else {
                        ToastUtil.showToast("再想想~");
                    }
                }
            });
            if (start < end) {
                wordToSpan.setSpan(tagText, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        textView.setText(wordToSpan);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        swordLoadingDialog = new SwordLoadingDialog(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        swordLoadingDialog.show();
        Observable.timer(2, TimeUnit.SECONDS)
                .compose(RxUtils.io2main())
                .subscribe(o -> {
                    swordLoadingDialog.dismiss();
                }, throwable -> {

                });
    }
}

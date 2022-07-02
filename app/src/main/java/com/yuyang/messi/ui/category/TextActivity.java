package com.yuyang.messi.ui.category;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.editfilter.MoneyInputTextWatcher;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.SnackBarUtil;
import com.yuyang.messi.view.ClickableTagText;
import com.yuyang.messi.view.shimmer.Shimmer;
import com.yuyang.messi.view.shimmer.ShimmerButton;
import com.yuyang.messi.view.shimmer.ShimmerTextView;
import com.yuyang.messi.view.toggle.RevealFollowButton;
import com.yuyang.messi.view.toggle.RevealFrameLayout;
import com.yuyang.messi.view.toggle.ShSwitchView;
import com.yuyang.messi.view.toggle.SlideSwitch;
import com.yuyang.messi.widget.MentionEditText;
import com.yuyang.messi.widget.autolinklibrary.AutoLinkMode;
import com.yuyang.messi.widget.autolinklibrary.AutoLinkOnClickListener;
import com.yuyang.messi.widget.autolinklibrary.AutoLinkTextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 创建者: yuyang
 * 创建日期: 2015-08-03
 * 创建时间: 09:46
 *
 * @author yuyang
 * @version 1.0
 */
// TODO: 2021/12/1   上标：SuperscriptSpan   下表：SubscriptSpan

public class TextActivity extends AppBaseActivity {

    /**
     * 文字色渐变TextView
     */
    private ShimmerTextView shimmerTextView;

    /**
     * 开关 view
     */
    private ShSwitchView switchView0;

    /**
     * TextView控制shimmer
     */
    private Shimmer shimmerTv;


    /**
     * 文字色渐变Button
     */
    private ShimmerButton shimmerButton;

    /**
     * 开关 view
     */
    private SlideSwitch switchView1;

    /**
     * Button控制shimmer
     */
    private Shimmer shimmerBtn;

    private RevealFollowButton revealFollowButton;
    private RevealFrameLayout revealFrameLayout;

    private MentionEditText mentionEditText;

    private AutoLinkTextView autoLinkTextView;

    /**
     * 测试字体颜色样式 text view
     */
    private TextView textView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_text;
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
        headerLayout.showTitle("文本");

        switchView0 = findViewById(R.id.activity_text_switch_view0);
        switchView1 = findViewById(R.id.activity_text_switch_view1);
        switchView1.setShapeType(SlideSwitch.SHAPE_CIRCLE);
        switchView1.setState(false);
        switchView1.setSlideable(true);

        shimmerTextView = findViewById(R.id.activity_text_shimmer_text);
        shimmerTextView.setText("伊布拉西诺维奇");
        shimmerTextView.setTextColor(getResources().getColor(R.color.red));//无动画时字体颜色
        shimmerTextView.setPrimaryColor(getResources().getColor(R.color.blue));//有动画时字体颜色
        shimmerTextView.setReflectionColor(getResources().getColor(R.color.yellow));//有动画时shimmer颜色

        shimmerButton = findViewById(R.id.activity_text_shimmer_button);
        shimmerButton.setText("Messi");
        shimmerButton.setTextColor(getResources().getColor(R.color.textPrimary));
        shimmerButton.setBackgroundColor(getResources().getColor(R.color.red));
        shimmerButton.setPrimaryColor(getResources().getColor(R.color.green));
        shimmerButton.setReflectionColor(getResources().getColor(R.color.blue));

        revealFollowButton = findViewById(R.id.activity_text_revealFollowBtn);
        revealFrameLayout = findViewById(R.id.activity_text_revealFrameLyt);

        /**
         * MentionEditText
         */
        mentionEditText = findViewById(R.id.activity_text_mentionEdit);
        mentionEditText.setMentionTextColor(Color.RED); //optional, set highlight color of mention string
        mentionEditText.setPattern(MentionEditText.DEFAULT_MENTION_PATTERN); //optional, set regularExpression
        mentionEditText.setOnMentionInputListener(new MentionEditText.OnMentionInputListener() {
            @Override
            public void onMentionCharacterInput() {
                //call when '@' character is inserted into EditText
                List<String> mentionList = mentionEditText.getMentionList(true); //get a list of mention string
                SnackBarUtil.makeShort(mentionEditText, mentionList.size() + " @ inserted!").show();
            }
        });

        /**
         * AutoLinkTextView
         */
        autoLinkTextView = findViewById(R.id.activity_text_autoLinkTextView);
        autoLinkTextView.setUnderLineEnabled(true);
        autoLinkTextView.addAutoLinkMode(
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_PHONE,
                AutoLinkMode.MODE_URL,
                AutoLinkMode.MODE_MENTION,
                AutoLinkMode.MODE_CUSTOM);
        autoLinkTextView.setCustomRegex("\\sAllo\\b");
        autoLinkTextView.setHashtagModeColor(ContextCompat.getColor(this, R.color.red));
        autoLinkTextView.setPhoneModeColor(ContextCompat.getColor(this, R.color.yellow));
        autoLinkTextView.setCustomModeColor(ContextCompat.getColor(this, R.color.blue));
        autoLinkTextView.setMentionModeColor(ContextCompat.getColor(this, R.color.green));
        autoLinkTextView.setAutoLinkText("Whether it’s planning a night out or just catching up, we all rely on #messaging to stay in @touch with friends and loved ones. But too often we have to hit pause on our #conversations — whether it’s to check the status of a flight or look up that new restaurant and dummy number for test 801-691-7894. So we created a messaging app that helps you keep your @conversation going, by providing assistance when you need it, https://google.com. Today, we’re releasing Allo and dummy number for test (808) 533-0075, and email for testing armcha01@gmail.com, a new smart messaging app for Android and iOS that helps you say more and do more right in your chats.Allo can help you make plans, find @information, and express yourself more easily in chat. And the more you use it, the more it improves over time.Allo makes it easier for you to #respond quickly and keep the #conversation going, even when you’re on the go. Chat is more than just text and dummy number for test +37493023017, so we’ve created a rich canvas for you to express yourself in Google Allo. Our web site https://allo.google.com/. You can make emojis and text larger or smaller in size by simply dragging the send button up or down.");
        autoLinkTextView.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
            @Override
            public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                ToastUtil.showToast(matchedText + "\nMode is: " + autoLinkMode.toString());
            }
        });


        textView = findViewById(R.id.activity_text_text0);
        String source = "<u>下划线</u> <i>斜体</i> <font color=#FF0000>红色</font> <font color='#0000FF'>蓝色</font> 普通 <u><font color=#FF0000>(下划线加红色)</font></u>";
        textView.setText(Html.fromHtml(source));

        TextView textView1 = findViewById(R.id.activity_text_text1);
        TextView textView2 = findViewById(R.id.activity_text_text2);
        TextView textView3 = findViewById(R.id.activity_text_text3);
        TextView textView4 = findViewById(R.id.activity_text_text4);

        textView1.setText("这是百度，这是baidu。。。");

        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return "";
            }
        };
        Pattern pattern1 = Pattern.compile("百度|baidu");
        String termsURL = "http://sobrr.life";
        Linkify.addLinks(textView1, pattern1, termsURL, null, filter);

        initSpan(textView2);
        initClickableSpan(textView3);

        EditText moneyEdit = findViewById(R.id.activity_text_moneyEdit);
        MoneyInputTextWatcher.bindEdit(moneyEdit);
//        moneyEdit.setFilters(new InputFilter[]{new MoneyFilter()});
    }

    private void initEvents() {

        switchView0.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                toggleAnimationShimmerText();
            }
        });

        switchView1.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                toggleAnimationShimmerButton();
            }

            @Override
            public void close() {
                toggleAnimationShimmerButton();
            }
        });
    }

    private void toggleAnimationShimmerText() {
        if (shimmerTv != null && shimmerTv.isAnimating()) {
            shimmerTv.cancel();
        } else {
            shimmerTv = new Shimmer();
            shimmerTv.start(shimmerTextView);
        }
    }

    private void toggleAnimationShimmerButton() {
        if (shimmerBtn != null && shimmerBtn.isAnimating()) {
            shimmerBtn.cancel();
        } else {
            shimmerBtn = new Shimmer();
            shimmerBtn.start(shimmerButton);
        }
    }

    /**
     * http://blog.csdn.net/lan410812571/article/details/9083023
     *
     * @param textView
     */
    private void initSpan(TextView textView) {
        SpannableString ss = new SpannableString("红色打电话斜体删除线绿色下划线图片:.");
        //用颜色标记文本
        ss.setSpan(new ForegroundColorSpan(Color.RED), 0, 2,
                //setSpan时需要指定的 flag,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括).
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //用超链接标记文本
        ss.setSpan(new URLSpan("tel:4155551212"), 2, 5,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //用样式标记文本（斜体）
        ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 5, 7,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //用删除线标记文本
        ss.setSpan(new StrikethroughSpan(), 7, 10,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //用下划线标记文本
        ss.setSpan(new UnderlineSpan(), 10, 15,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //用颜色标记
        ss.setSpan(new ForegroundColorSpan(Color.GREEN), 10, 12,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //获取Drawable资源
        Drawable d = getResources().getDrawable(R.mipmap.ic_launcher);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        //创建ImageSpan
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        //用ImageSpan替换文本
        ss.setSpan(span, 18, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initClickableSpan(TextView textView) {

        String totalText = "hehehe呵呵呵 @于洋 #哈哈哈 什么什么什么 @haha";

        Spannable wordToSpan = new SpannableString(totalText);
        Pattern p = Pattern.compile("#\\w+");// \w : 一个或多个字符 至少一个
        Matcher tagMatcher = p.matcher(totalText);
        while (tagMatcher.find()) {

            int start = tagMatcher.start();
            int end = tagMatcher.end();
            final String matchString = totalText.substring(start, end);
            ClickableTagText tagText = new ClickableTagText(Color.CYAN, true);
            tagText.setOnClickLister(new ClickableTagText.OnClickLister() {
                @Override
                public void OnClick(View widget) {
                    ToastUtil.showToast(matchString);
                }
            });
            if (start < end) {
                wordToSpan.setSpan(tagText, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

        }

        Pattern userPattern = Pattern.compile("@\\S+");// \\S : 匹配任何空白字符  \r \n 等
        Matcher userMatcher = userPattern.matcher(totalText);
        while (userMatcher.find()) {

            int start = userMatcher.start();
            int end = userMatcher.end();

            final String atUserName = totalText.substring(start + 1, end);

            if (wordToSpan.getSpans(start, end, ClickableTagText.class).length == 0) {
                ClickableTagText tagText = new ClickableTagText(getResources().getColor(R.color.colorPrimary), false);

                tagText.setOnClickLister(new ClickableTagText.OnClickLister() {
                    @Override
                    public void OnClick(View widget) {
                        ToastUtil.showToast(atUserName);
                    }
                });
                if (start < end) {
                    wordToSpan.setSpan(tagText, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }

            }
        }

        textView.setText(wordToSpan);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}


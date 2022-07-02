package com.yuyang.messi.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * see:
 * MoneyFilter
 * MoneyInputTextWatcher
 */
public class InputUtil {

    /**
     * 只允许数字字母汉字
     */
    public static InputFilter letterOrDigitFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            source = source.toString().replaceAll( "[^a-zA-Z0-9\u4E00-\u9FA5]", "");
//            for (int i = start; i < end; i++) {
//                if (!Character.isLetterOrDigit(source.charAt(i))) {
//                    return "";
//                }
//            }
            return source;
        }
    };

    public static InputFilter emojiFilter = new InputFilter() {

        Pattern emoji = Pattern.compile(
                "[\ue000-\uf8ff]|[\\x{1f300}-\\x{1f7ff}]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher emojiMatcher = emoji.matcher(source);

            if (emojiMatcher.find()) {

                return "";

            }
            return null;

        }
    };

    /**
     * 过滤字符
     * <p/>
     * Example:
     * afterTextChanged(Editable s) {
     * String str = editText.getText().toString();
     * String filterStr = StringUtil.stringFilter(str, "[^a-zA-Z0-9]");
     * if (!filterStr.equals(str)) {
     * editText.setText(filterStr);
     * editText.setSelection(filterStr.length());
     * }
     * }
     *
     * @param str
     * @param regEx
     * @return
     * @throws PatternSyntaxException
     */
    public static String stringFilter(String str, String regEx) throws PatternSyntaxException {

//		regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥¥%……&*（）——+|{}【】‘；：”“’。，、？\\-_]";	// 不允许字符
//		regEx = "[^\\p{han}]";			// 只允许汉字(更精确)
//		regEx = "[^\u4E00-\u9FA5]";		// 只允许汉字
//		regEx = "[^a-zA-Z0-9]";			// 只允许大小写英文和数字
//		regEx = "[^a-zA-Z\\p{han}]";	// 只允许汉字和英文
//		regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";	// 只允许字母、数字和汉字

        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        return m.replaceAll("").trim();
    }
}

package com.yamap.lib_chat.filter;

import android.text.Spannable;
import android.widget.EditText;

import com.sj.emoji.EmojiDisplay;
import com.sj.emoji.EmojiSpan;
import com.yamap.lib_chat.keyboard.interfaces.EmoticonFilter;
import com.yamap.lib_chat.keyboard.utils.KeyboardUtil;

import java.util.regex.Matcher;

public class EmojiFilter extends EmoticonFilter {

    private int emojiSize = -1;

    @Override
    public void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter) {
        emojiSize = emojiSize == -1 ? KeyboardUtil.getFontHeight(editText) : emojiSize;
        clearSpan(editText.getText(), start, text.toString().length());
        Matcher m = EmojiDisplay.getMatcher(text.toString().substring(start, text.toString().length()));
        if (m != null) {
            while (m.find()) {
                String emojiHex = Integer.toHexString(Character.codePointAt(m.group(), 0));
//                EmojiDisplay.filterFromResource(editText.getContext(), editText.getText(), emojiHex, emojiSize, start + m.start(), start + m.end());
            }
        }
    }

    private void clearSpan(Spannable spannable, int start, int end) {
        if (start == end) {
            return;
        }
        EmojiSpan[] oldSpans = spannable.getSpans(start, end, EmojiSpan.class);
        for (int i = 0; i < oldSpans.length; i++) {
            spannable.removeSpan(oldSpans[i]);
        }
    }
}

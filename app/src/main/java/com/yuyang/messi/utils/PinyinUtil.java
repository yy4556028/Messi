package com.yuyang.messi.utils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {

    /**
     * 获取汉字串拼音首字母缩写，英文字符不变
     * 于洋 -> yy
     */
    public static String getHeadSpell(String chinese) {
        if (TextUtils.isEmpty(chinese)) return null;
        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        for (char c : chars) {
            if (c > 128) {
                String temp = getPinyinByHanzi(c, format);
                if (temp != null) {
                    sb.append(temp.charAt(0));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString().replaceAll("\\W", "").trim();
    }

    /**
     * 获取汉字串拼音，英文字符不变
     */
    public static String getFullSpell(String chinese, String split) {
        if (TextUtils.isEmpty(chinese)) return null;
        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c > 128) {
                String temp = getPinyinByHanzi(c, format);
                if (temp != null) {
                    sb.append(temp);
                }
            } else {
                sb.append(c);
            }

            if (!TextUtils.isEmpty(split) && i < chars.length - 1) {
                sb.append(split);
            }
        }
        return sb.toString();
    }

    private static String getPinyinByHanzi(char c, HanyuPinyinOutputFormat defaultFormat) {
        String result = null;
        if ("单".toCharArray()[0] == c) {
            return "shan";
        } else if ("仇".toCharArray()[0] == c) {
            return "qiu";
        } else if ("曾".toCharArray()[0] == c) {
            return "zeng";
        } else {
            try {
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat);
                if (temp != null) {
                    result = temp[0];
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
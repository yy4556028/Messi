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
     * 获取汉字的首字母拼音
     *
     * @param chinese 要转换的汉字字符串
     * @return 首字母拼音字符串
     * 于洋 -> yy
     */
    public static String getFirstLetter(String chinese) {
        if (TextUtils.isEmpty(chinese)) return null;
        StringBuilder firstLetter = new StringBuilder();
        char[] charArray = chinese.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //将 “ü” 显示为 “v”
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        for (char c : charArray) {
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                String temp = getPinyinByHanzi(c, format);
                if (temp != null) {
                    firstLetter.append(temp.charAt(0));
                }
            } else {
                firstLetter.append(c);
            }
        }
        return firstLetter.toString().replaceAll("\\W", "").trim();
    }

    /**
     * 将汉字转换为拼音（全拼）
     *
     * @param chinese 要转换的汉字字符串
     * @param split   分隔符，默认为空格
     * @return 转换后的拼音字符串
     */
    public static String getFullSpell(String chinese, String split) {
        if (TextUtils.isEmpty(chinese)) return null;
        StringBuilder pinyin = new StringBuilder();
        char[] charArray = chinese.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //将 “ü” 显示为 “v”
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            // 判断是否为汉字
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                String temp = getPinyinByHanzi(c, format);
                if (temp != null) {
                    pinyin.append(temp);
                }
//                try {
//                    // 获取该汉字的拼音数组，因为一个汉字可能有多个读音
//                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
//                    if (pinyinArray != null && pinyinArray.length > 0) {
//                        // 这里取第一个读音
//                        pinyin.append(pinyinArray[0]);
//                    }
//                } catch (BadHanyuPinyinOutputFormatCombination e) {
//                    e.printStackTrace();
//                }
            } else {
                // 非汉字字符直接添加
                pinyin.append(c);
            }

            if (!TextUtils.isEmpty(split) && i < charArray.length - 1) {
                pinyin.append(split);
            }
        }
        return pinyin.toString();
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
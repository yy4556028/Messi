package com.yuyang.messi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by _SOLID
 * Date:2016/5/10
 * Time:10:36
 */
public class RegexUtil {

    /**
     * 判断是否用户名
     * 中文，大小写字母，数字，下划线 2-15位
     * /u4e00-/u9fa5 (中文)
     * /x3130-/x318F (韩文
     * /xAC00-/xD7A3 (韩文)
     * /u0800-/u4e00 (日文)
     */
    public static boolean isUserName(String mobiles) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA5a-zA-Z0-9_-]{2,15}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断email格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 检查是否是Email
     *
     * @param email
     * @return
     */
    public static boolean isEmail2(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 判断是不是合法手机号码
     */
    public static boolean isPhoneNumber(String handset) {
        try {
            if (!handset.startsWith("1")) {
                return false;
            }
            if (handset == null || handset.length() != 11) {
                return false;
            }
            String check = "^[0123456789]+$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(handset);
            boolean isMatched = matcher.matches();
            if (isMatched) {
                return true;
            } else {
                return false;
            }
        } catch (RuntimeException e) {
            return false;
        }
    }

    public String transformPhoneNum(String phoneNum) {
        if (isPhoneNumber(phoneNum)) {
            return phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        } else {
            return phoneNum;
        }
    }

    /**
     * 判断输入的字符串是否为纯汉字
     *
     * @param str 传入的字符窜
     * @return 如果是纯汉字返回true, 否则返回false
     */
    public static boolean isChinese(String str) {
        Pattern pattern = Pattern.compile("[\u0391-\uFFE5]+$");
        return pattern.matcher(str).matches();
    }

    /**
     * 校验是否纯数字
     */
    public static boolean isNumber(String str) {
        return str.matches("[0-9]+");
    }

    /**
     * 判断是否是字母或者数据
     * 传入""返回true
     */
    public static boolean isNumberOrLetters(String str) {
        return Pattern.matches("^[0-9a-zA-Z]*$", str);
    }

    /**
     * 判断是否为整数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否为浮点数，包括double和float
     *
     * @param str 传入的字符串
     * @return 是浮点数返回true, 否则返回false
     */
    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 截取非数字
     *
     * @param content
     * @return
     */

    public static String findNotNumbers(String content) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("").trim();
    }

    /**
     * 截取数字
     *
     * @param content
     * @return
     */
    public static String findNumbers(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("").trim();
    }
}

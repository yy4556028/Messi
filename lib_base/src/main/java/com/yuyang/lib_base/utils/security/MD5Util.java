package com.yuyang.lib_base.utils.security;

import java.security.MessageDigest;

/**
 * RSA详见
 * http://blog.csdn.net/yanzi1225627/article/details/26508035
 * https://www.cnblogs.com/wxh04/p/4253305.html
 */
public class MD5Util {

    public final static String md5(String input) {
        return digest("MD5", input);
    }

    public final static String md5(byte[] input) {
        return digest("MD5", input);
    }

    public final static String digest(String algorithm, byte[] input) {
        byte[] bytes;
        try {
            bytes = MessageDigest.getInstance(algorithm).digest(input);
            return bytesToHexString(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    public final static String digest(String algorithm, String input) {
        byte[] bytes;
        try {
            bytes = MessageDigest.getInstance(algorithm).digest(input.getBytes("UTF-8"));
            return bytesToHexString(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (byte aByte : bytes) {
            String hv = Integer.toHexString(aByte & 0xFF);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
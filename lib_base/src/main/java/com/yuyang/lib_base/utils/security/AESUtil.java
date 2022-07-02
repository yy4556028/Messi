/*
 * Copyright (C), 2002-2019, 苏宁易购电子商务有限公司
 * FileName: com.suning.ass.gateway.core.util
 * Author:   19040700
 * Date:     2019/8/8 20:07
 * Description: //模块目的、功能描述
 * History: //修改记录
 *
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.yuyang.lib_base.utils.security;

import android.text.TextUtils;
import android.util.Base64;

import com.yuyang.lib_base.utils.LogUtil;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author 19040700
 */
public class AESUtil {

    private final static String HEX = "0123456789ABCDEF";

    /**
     * @return 动态生成秘钥
     */
    public static String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance("SHA1PRNG");
            byte[] bytes_key = new byte[20];
            localSecureRandom.nextBytes(bytes_key);
            String str_key = toHex(bytes_key);
            return str_key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createSecretKey(byte[] seed) {
        try {
            //密钥生成器
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            //for android
            SecureRandom sr = null;

            // 在4.2以上版本中，SecureRandom获取方式发生了改变
            // Android  6.0 以上
            if (android.os.Build.VERSION.SDK_INT > 23) {
                sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
                //4.2及以上
            } else if (android.os.Build.VERSION.SDK_INT >= 17) {
                sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            } else {
                sr = SecureRandom.getInstance("SHA1PRNG");
            }

            sr.setSeed(seed);

            //默认128，获得无政策权限后可为192或256
            keyGen.init(128, sr);
            //生成密钥
            SecretKey secretKey = keyGen.generateKey();
            //密钥字节数组
            byte[] key = secretKey.getEncoded();

            return Base64.encodeToString(key, Base64.NO_WRAP);
        } catch (Exception e) {
            LogUtil.e(AESUtil.class.getSimpleName(), "创建秘钥失败");
        }
        return null;
    }

    public static String createSecretKey(String password) {
        try {
            //密钥生成器
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            //默认128, 获得无政策权限后可为192或256, 利用用户密码作为随机数初始化出
            if (TextUtils.isEmpty(password)) {
                keyGen.init(128);
            } else {
                keyGen.init(128, new SecureRandom(password.getBytes()));
            }
            //生成密钥
            SecretKey secretKey = keyGen.generateKey();
            //密钥字节数组
            byte[] key = secretKey.getEncoded();

            return Base64.encodeToString(key, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(AESUtil.class.getSimpleName(), "创建秘钥失败");
        }
        return null;
    }

    public static String encrypt(String text, String key) throws Exception {
        byte[] keyData = Base64.decode(key, Base64.NO_WRAP);
        //恢复密钥
        SecretKey secretKey = new SecretKeySpec(keyData, "AES");
        //Cipher完成加密或解密工作类
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //对Cipher初始化，解密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        //加密data
        byte[] cipherByte = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(cipherByte, Base64.NO_WRAP);
    }

    public static String decrypt(String text, String key) throws Exception {

        byte[] data = Base64.decode(text, Base64.NO_WRAP);
        byte[] keyData = Base64.decode(key, Base64.NO_WRAP);
        //恢复密钥
        SecretKey secretKey = new SecretKeySpec(keyData, "AES");
        //Cipher完成加密或解密工作类
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //对Cipher初始化，解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        //解密data
        byte[] cipherByte = cipher.doFinal(data);
        return new String(cipherByte, StandardCharsets.UTF_8);

    }

    public static void main(String[] args) {

        try {
            String key = createSecretKey("");

            System.out.println("秘钥：" + key);
            String text = "[\"9001914084\"]";

            System.out.println("明文：");
            System.out.println(text);
            String r1 = encrypt(text, key);
            System.out.println("密文：");
            System.out.println(r1);
            String r2 = decrypt(r1, key);
            System.out.println("解密：");
            System.out.println(r2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //二进制转字符
    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    public static class CryptoProvider extends Provider {
        /**
         * Creates a Provider and puts parameters
         */
        public CryptoProvider() {
            super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
            put("SecureRandom.SHA1PRNG",
                    "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
        }
    }
}

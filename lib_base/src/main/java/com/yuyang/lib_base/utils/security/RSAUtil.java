/*
 * Copyright (C), 2002-2019, 苏宁易购电子商务有限公司
 * FileName: com.suning.ass.gateway.core.util
 * Author:   19040700
 * Date:     2019/8/8 15:58
 * Description: //模块目的、功能描述
 * History: //修改记录
 *
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.yuyang.lib_base.utils.security;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 非对称加密
 *
 * @author 19040700
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class RSAUtil {

    public static final String KEY_ALGORITHM = "RSA";
    /**
     * 秘钥默认长度
     */
    public static final int KEY_SIZE = 1024;
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = KEY_SIZE / 8 - 11;

    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048 一般1024
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(keyLength);
            KeyPair keyPair = keyPairGen.generateKeyPair();

            // 公钥
//            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 私钥
//            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用公钥对字符串进行加密
     *
     * @param data 原文
     * @param key  publicKey
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        // 对公钥解密
        byte[] keyBytes = Base64.decode(key, Base64.NO_WRAP);

        // 得到公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        /**
         * 对数据加密
         * java默认"RSA"="RSA/ECB/PKCS1Padding"
         * android 用"RSA/None/PKCS1Padding"
         * Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
         */
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        return getBytes(data, cipher, MAX_ENCRYPT_BLOCK);//分段加密
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     *
     * @param data      待解密数据
     * @param publicKey 密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPublicKey(String type, byte[] data, byte[] publicKey) throws Exception {
        // 得到公钥
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey keyPublic = keyFactory.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, keyPublic);
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(String type, byte[] data, byte[] privateKey) throws Exception {
        // 得到私钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey keyPrivate = keyFactory.generatePrivate(keySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, keyPrivate);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param key
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = Base64.decode(key, Base64.NO_WRAP);

        // 取得私钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(keySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    public static String encrypt(String text, String key) throws Exception {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(encryptByPublicKey(data, key), Base64.NO_WRAP);
    }

    public static String decrypt(String text, String key) throws Exception {
        byte[] data = Base64.decode(text, Base64.NO_WRAP);
        return new String(decryptByPrivateKey(data, key), StandardCharsets.UTF_8);
    }

    /**
     * 分段加/解密
     *
     * @param content
     * @param cipher
     */
    private static byte[] getBytes(byte[] content, Cipher cipher, final int maxBlock) throws Exception {
        int inputLen = content.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加/解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxBlock) {
                cache = cipher.doFinal(content, offSet, maxBlock);
            } else {
                cache = cipher.doFinal(content, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxBlock;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
}

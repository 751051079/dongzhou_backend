package com.smarterp.common.core.utils.mercado;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;

/**
 * @author juntao.li
 * @ClassName MercadoSecureUtil
 * @description AES对称加密
 * @date 2023/4/23 22:18
 * @Version 1.0
 */
public class MercadoSecureUtil {

    public static final String KEY = "1650148743539986432";

    /**
     * AES加密方法
     *
     * @param key          加密秘钥(注意秘钥必须是16位字符串)
     * @param clearContent 明文
     * @return 密文
     */
    public static String encryptMercado(String key, String clearContent) {
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        byte[] keys = key.getBytes(StandardCharsets.UTF_8);
        AES aes = SecureUtil.aes(keys);
        return aes.encryptHex(clearContent);
    }

    /**
     * AES解密方法
     *
     * @param key           加密秘钥(注意秘钥必须是16位字符串)
     * @param cipherContent 密文
     * @return 明文
     */
    public static String decryptMercado(String key, String cipherContent) {
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        byte[] keys = key.getBytes(StandardCharsets.UTF_8);
        AES aes = SecureUtil.aes(keys);
        return aes.decryptStr(cipherContent, CharsetUtil.CHARSET_UTF_8);
    }

    public static void main(String[] args) {
        System.out.println(decryptMercado("1779124173529026560","d585cee819d2c4afda3773e5416ef8a768f5ab73e66aab841ee52e20b9fe851d5b5f80cfb45f24d53479ea2c40ead9aafb65881c56c5eda3750888803ad131ee5b72f932267c0d1e926200a8dabc260a"));
    }

}

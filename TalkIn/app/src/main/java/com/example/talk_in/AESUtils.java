package com.example.talk_in;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.regex.Pattern;

public class AESUtils {

    private static final byte[] keyValue =
            new byte[]{'c', 'o', 'd', 'i', 'n', 'g', 'a', 'f', 'f', 'a', 'i', 'r', 's', 'c', 'o', 'm'};
    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9A-Fa-f]+$");
    private static final String TAG = "AESUtils";

    public static String encrypt(String cleartext) throws Exception {
        byte[] rawKey = getRawKey();
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public static String decrypt(String encrypted) throws Exception {
        if (!isHex(encrypted)) {
            throw new IllegalArgumentException("Input string is not valid hexadecimal");
        }
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(enc);
        return new String(result);
    }

    private static byte[] getRawKey() throws Exception {
        SecretKey key = new SecretKeySpec(keyValue, "AES");
        return key.getEncoded();
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKey skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] encrypted) throws Exception {
        SecretKey skeySpec = new SecretKeySpec(keyValue, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null) return "";
        StringBuilder result = new StringBuilder(2 * buf.length);
        for (byte b : buf) {
            appendHex(result, b);
        }
        return result.toString();
    }

    private static final String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuilder sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    private static boolean isHex(String s) {
        return s != null && HEX_PATTERN.matcher(s).matches();
    }
}

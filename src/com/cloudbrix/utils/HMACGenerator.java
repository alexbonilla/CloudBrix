/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudbrix.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Alex
 */
public class HMACGenerator {

    final static protected char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String computeSignatureHmacSHA1(String baseString, String keyString) throws GeneralSecurityException, UnsupportedEncodingException {

        SecretKey secretKey = null;

        byte[] keyBytes = keyString.getBytes();
        secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");

        mac.init(secretKey);

        byte[] text = baseString.getBytes();

        return new String(Base64.encode(mac.doFinal(text))).trim();
    }

    public static String computeSignatureHmacMD5(String baseString, String keyString) throws GeneralSecurityException, UnsupportedEncodingException {

        SecretKey secretKey = null;

        byte[] keyBytes = keyString.getBytes();
        secretKey = new SecretKeySpec(keyBytes, "HmacMD5");

        Mac mac = Mac.getInstance("HmacMD5");

        mac.init(secretKey);

        byte[] text = baseString.getBytes();

        return new String(Base64.encode(mac.doFinal(text))).trim();
    }

    public static String hmacSha1(String value, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
            //byte[] hexBytes = bytesToHex(rawHmac);
            //  Covert array of Hex bytes to a String
            return bytesToHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getHMAC(String key, String message) {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacMD5");
        Mac mac;
        String hash = null;
        try {
            mac = Mac.getInstance("HmacMD5");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(message.getBytes());
            hash = Base64.encode(rawHmac);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-­‐generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {// TODO Auto-­‐generated catch block
            e.printStackTrace();
        }
        return hash;
    }
}

package com.solaredge.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import android.content.Context;
import android.util.Base64;

import com.solaredge.config.AppConfig;

public class EncryptUtil {

	public static String encrypt(Context con, String src) {
		String key = AppConfig.SECURITY_KEY.substring(0, 23);
		LogX.trace("Solar", key);
		DESedeKeySpec dks;
		byte[] b = null;
		try {
			dks = new DESedeKeySpec(key.getBytes("UTF-8"));

			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);

			Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, securekey);
			b = cipher.doFinal(src.getBytes());
		} catch (InvalidKeyException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	public static String decrypt(Context con, String src) {
		String key = AppConfig.SECURITY_KEY.substring(0, 7);
		byte[] bytesrc = Base64.decode(src, Base64.DEFAULT);
		DESedeKeySpec dks;
		byte[] retByte = null;
		try {
			dks = new DESedeKeySpec(key.getBytes("UTF-8"));

			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);

			Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, securekey);
			retByte = cipher.doFinal(bytesrc);
		} catch (InvalidKeyException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		return new String(retByte);
	}

	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = (md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}
	
	public final static String MD5_2(String s) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	public static String desCrypto(String src, String password) {
		byte[] b = null;
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密,正式执行加密操作
			b = cipher.doFinal(src.getBytes("UTF-8"));

			return new String(b);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encrypt(byte[] data, byte[] key) {
		try {
			int paddinglen = 0;
			if (data.length % 8 != 0) {
				paddinglen = 8 - data.length % 8;
			}
			int len = data.length + paddinglen;
			byte[] b = new byte[len];
			for (int i = 0; i < len; i++) {
				if (i < data.length)
					b[i] = data[i];
				else
					b[i] = 0;
			}

			SecureRandom sr = new SecureRandom();
			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(dks);

			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
			byte encryptedData[] = cipher.doFinal(b);
			String encryptedHexStr = "";

			for (int i = 0; i < encryptedData.length; i++) {
				String hex = Integer.toHexString(encryptedData[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				encryptedHexStr += hex.toLowerCase();
			}
			return encryptedHexStr;
		} catch (Exception e) {
			System.err.println("DES算法，加密数据出错!");
			e.printStackTrace();
		}
		return null;
	}

}

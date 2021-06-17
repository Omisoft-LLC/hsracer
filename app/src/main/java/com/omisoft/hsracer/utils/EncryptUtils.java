package com.omisoft.hsracer.utils;

import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Generic encryption utils
 */
public final class EncryptUtils {

  private EncryptUtils() {
    throw new UnsupportedOperationException(Utils.NO_INIT);
  }

  ///////////////////////////////////////////////////////////////////////////
  // 哈希相关
  ///////////////////////////////////////////////////////////////////////////

  /**
   * MD2
   */
  public static String encryptMD2ToString(final String data) {
    return encryptMD2ToString(data.getBytes());
  }

  /**
   * MD2
   */
  public static String encryptMD2ToString(final byte[] data) {
    return bytes2HexString(encryptMD2(data));
  }

  /**
   * MD2
   */
  public static byte[] encryptMD2(final byte[] data) {
    return hashTemplate(data, "MD2");
  }

  /**
   * MD5
   */
  public static String encryptMD5ToString(final String data) {
    return encryptMD5ToString(data.getBytes());
  }

  /**
   * MD5
   */
  public static String encryptMD5ToString(final String data, final String salt) {
    return bytes2HexString(encryptMD5((data + salt).getBytes()));
  }

  /**
   * MD5
   */
  public static String encryptMD5ToString(final byte[] data) {
    return bytes2HexString(encryptMD5(data));
  }

  /**
   * MD5
   */
  public static String encryptMD5ToString(final byte[] data, final byte[] salt) {
    if (data == null || salt == null) {
      return null;
    }
    byte[] dataSalt = new byte[data.length + salt.length];
    System.arraycopy(data, 0, dataSalt, 0, data.length);
    System.arraycopy(salt, 0, dataSalt, data.length, salt.length);
    return bytes2HexString(encryptMD5(dataSalt));
  }

  /**
   * MD5
   **/
  public static byte[] encryptMD5(final byte[] data) {
    return hashTemplate(data, "MD5");
  }

  /**
   * MD5
   */
  public static String encryptMD5File2String(final String filePath) {
    File file = isSpace(filePath) ? null : new File(filePath);
    return encryptMD5File2String(file);
  }

  /**
   * MD5
   */
  public static byte[] encryptMD5File(final String filePath) {
    File file = isSpace(filePath) ? null : new File(filePath);
    return encryptMD5File(file);
  }

  /**
   * MD5
   */
  public static String encryptMD5File2String(final File file) {
    return bytes2HexString(encryptMD5File(file));
  }

  /**
   * MD5
   */
  public static byte[] encryptMD5File(final File file) {
    if (file == null) {
      return null;
    }
    FileInputStream fis = null;
    DigestInputStream digestInputStream;
    try {
      fis = new FileInputStream(file);
      MessageDigest md = MessageDigest.getInstance("MD5");
      digestInputStream = new DigestInputStream(fis, md);
      byte[] buffer = new byte[256 * 1024];
      while (true) {
        if (!(digestInputStream.read(buffer) > 0)) {
          break;
        }
      }
      md = digestInputStream.getMessageDigest();
      return md.digest();
    } catch (NoSuchAlgorithmException | IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      CloseUtils.closeIO(fis);
    }
  }

  /**
   * SHA1
   */
  public static String encryptSHA1ToString(final String data) {
    return encryptSHA1ToString(data.getBytes());
  }

  /**
   * SHA1
   *
   * @return 16
   */
  public static String encryptSHA1ToString(final byte[] data) {
    return bytes2HexString(encryptSHA1(data));
  }

  /**
   * SHA1
   */
  public static byte[] encryptSHA1(final byte[] data) {
    return hashTemplate(data, "SHA1");
  }

  /**
   * SHA224
   */
  public static String encryptSHA224ToString(final String data) {
    return encryptSHA224ToString(data.getBytes());
  }

  /**
   * SHA224
   */
  public static String encryptSHA224ToString(final byte[] data) {
    return bytes2HexString(encryptSHA224(data));
  }

  /**
   * SHA224
   */
  public static byte[] encryptSHA224(final byte[] data) {
    return hashTemplate(data, "SHA224");
  }

  /**
   * SHA256
   */
  public static String encryptSHA256ToString(final String data) {
    return encryptSHA256ToString(data.getBytes());
  }

  /**
   * SHA256
   */
  public static String encryptSHA256ToString(final byte[] data) {
    return bytes2HexString(encryptSHA256(data));
  }

  /**
   * SHA256
   */
  public static byte[] encryptSHA256(final byte[] data) {
    return hashTemplate(data, "SHA256");
  }

  /**
   * SHA384
   */
  public static String encryptSHA384ToString(final String data) {
    return encryptSHA384ToString(data.getBytes());
  }

  /**
   * SHA384
   */
  public static String encryptSHA384ToString(final byte[] data) {
    return bytes2HexString(encryptSHA384(data));
  }

  /**
   * SHA384
   */
  public static byte[] encryptSHA384(final byte[] data) {
    return hashTemplate(data, "SHA384");
  }

  /**
   * SHA512
   */
  public static String encryptSHA512ToString(final String data) {
    return encryptSHA512ToString(data.getBytes());
  }

  /**
   * SHA512
   */
  public static String encryptSHA512ToString(final byte[] data) {
    return bytes2HexString(encryptSHA512(data));
  }

  /**
   * SHA512
   */
  public static byte[] encryptSHA512(final byte[] data) {
    return hashTemplate(data, "SHA512");
  }

  /**
   * hash
   */
  private static byte[] hashTemplate(final byte[] data, final String algorithm) {
    if (data == null || data.length <= 0) {
      return null;
    }
    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      md.update(data);
      return md.digest();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * HmacMD5
   */
  public static String encryptHmacMD5ToString(final String data, final String key) {
    return encryptHmacMD5ToString(data.getBytes(), key.getBytes());
  }

  /**
   * HmacMD5
   */
  public static String encryptHmacMD5ToString(final byte[] data, final byte[] key) {
    return bytes2HexString(encryptHmacMD5(data, key));
  }

  /**
   * HmacMD5
   */
  public static byte[] encryptHmacMD5(final byte[] data, final byte[] key) {
    return hmacTemplate(data, key, "HmacMD5");
  }

  /**
   * HmacSHA1
   */
  public static String encryptHmacSHA1ToString(final String data, final String key) {
    return encryptHmacSHA1ToString(data.getBytes(), key.getBytes());
  }

  /**
   * HmacSHA1
   */
  public static String encryptHmacSHA1ToString(final byte[] data, final byte[] key) {
    return bytes2HexString(encryptHmacSHA1(data, key));
  }

  /**
   * HmacSHA1
   */
  public static byte[] encryptHmacSHA1(final byte[] data, final byte[] key) {
    return hmacTemplate(data, key, "HmacSHA1");
  }

  /**
   * HmacSHA224
   */
  public static String encryptHmacSHA224ToString(final String data, final String key) {
    return encryptHmacSHA224ToString(data.getBytes(), key.getBytes());
  }

  /**
   * HmacSHA224
   */
  public static String encryptHmacSHA224ToString(final byte[] data, final byte[] key) {
    return bytes2HexString(encryptHmacSHA224(data, key));
  }

  /**
   * HmacSHA224
   */
  public static byte[] encryptHmacSHA224(final byte[] data, final byte[] key) {
    return hmacTemplate(data, key, "HmacSHA224");
  }

  /**
   * HmacSHA256
   */
  public static String encryptHmacSHA256ToString(final String data, final String key) {
    return encryptHmacSHA256ToString(data.getBytes(), key.getBytes());
  }

  /**
   * HmacSHA256
   */
  public static String encryptHmacSHA256ToString(final byte[] data, final byte[] key) {
    return bytes2HexString(encryptHmacSHA256(data, key));
  }

  /**
   * HmacSHA256
   */
  public static byte[] encryptHmacSHA256(final byte[] data, final byte[] key) {
    return hmacTemplate(data, key, "HmacSHA256");
  }

  /**
   * HmacSHA384
   */
  public static String encryptHmacSHA384ToString(final String data, final String key) {
    return encryptHmacSHA384ToString(data.getBytes(), key.getBytes());
  }

  /**
   * HmacSHA384
   */
  public static String encryptHmacSHA384ToString(final byte[] data, final byte[] key) {
    return bytes2HexString(encryptHmacSHA384(data, key));
  }

  /**
   * HmacSHA384
   */
  public static byte[] encryptHmacSHA384(final byte[] data, final byte[] key) {
    return hmacTemplate(data, key, "HmacSHA384");
  }

  /**
   * HmacSHA512
   */
  public static String encryptHmacSHA512ToString(final String data, final String key) {
    return encryptHmacSHA512ToString(data.getBytes(), key.getBytes());
  }

  /**
   * HmacSHA512
   */
  public static String encryptHmacSHA512ToString(final byte[] data, final byte[] key) {
    return bytes2HexString(encryptHmacSHA512(data, key));
  }

  /**
   * HmacSHA512
   */
  public static byte[] encryptHmacSHA512(final byte[] data, final byte[] key) {
    return hmacTemplate(data, key, "HmacSHA512");
  }

  /**
   * Hmac
   */
  private static byte[] hmacTemplate(final byte[] data, final byte[] key, final String algorithm) {
    if (data == null || data.length == 0 || key == null || key.length == 0) {
      return null;
    }
    try {
      SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
      Mac mac = Mac.getInstance(algorithm);
      mac.init(secretKey);
      return mac.doFinal(data);
    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  // AES
  public static String AES_Transformation = "AES/ECB/NoPadding";
  private static final String AES_Algorithm = "AES";


  /**
   * AES Base64
   *
   * @param key 16、24、32
   * @return Base64
   */
  public static byte[] encryptAES2Base64(final byte[] data, final byte[] key) {
    return base64Encode(encryptAES(data, key));
  }

  /**
   * AES 16
   *
   * @param key 16、24、32
   */
  public static String encryptAES2HexString(final byte[] data, final byte[] key) {
    return bytes2HexString(encryptAES(data, key));
  }

  /**
   * AES
   *
   * @param key 16、24、32
   */
  public static byte[] encryptAES(final byte[] data, final byte[] key) {
    return desTemplate(data, key, AES_Transformation, true);
  }

  /**
   * AES Base64
   *
   * @param data Base64
   * @param key 16、24、32
   * @return 明文
   */
  public static byte[] decryptBase64AES(final byte[] data, final byte[] key) {
    return decryptAES(base64Decode(data), key);
  }

  /**
   * AES
   *
   * @param key 16、24、32
   */
  public static byte[] decryptHexStringAES(final String data, final byte[] key) {
    return decryptAES(hexString2Bytes(data), key);
  }

  /**
   * AES
   *
   * @param key 16、24、32
   */
  public static byte[] decryptAES(final byte[] data, final byte[] key) {
    return desTemplate(data, key, AES_Transformation, false);
  }

  /**
   * DES
   *
   * @param transformation 转变
   * @param isEncrypt {@code true}:  {@code false}: 解密
   * @return 密文或者明文，适用于DES，3DES，AES
   */
  public static byte[] desTemplate(final byte[] data, final byte[] key,
      final String transformation, final boolean isEncrypt) {
    if (data == null || data.length == 0 || key == null || key.length == 0) {
      return null;
    }
    try {
      SecretKeySpec keySpec = new SecretKeySpec(key, AES_Algorithm);
      Cipher cipher = Cipher.getInstance(transformation);
      SecureRandom random = new SecureRandom();
      cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keySpec, random);
      return cipher.doFinal(data);
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
      'B', 'C', 'D', 'E', 'F'};

  private static String bytes2HexString(final byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    int len = bytes.length;
    if (len <= 0) {
      return null;
    }
    char[] ret = new char[len << 1];
    for (int i = 0, j = 0; i < len; i++) {
      ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
      ret[j++] = hexDigits[bytes[i] & 0x0f];
    }
    return new String(ret);
  }

  private static byte[] hexString2Bytes(String hexString) {
    if (isSpace(hexString)) {
      return null;
    }
    int len = hexString.length();
    if (len % 2 != 0) {
      hexString = "0" + hexString;
      len = len + 1;
    }
    char[] hexBytes = hexString.toUpperCase().toCharArray();
    byte[] ret = new byte[len >> 1];
    for (int i = 0; i < len; i += 2) {
      ret[i >> 1] = (byte) (hex2Dec(hexBytes[i]) << 4 | hex2Dec(hexBytes[i + 1]));
    }
    return ret;
  }

  private static int hex2Dec(final char hexChar) {
    if (hexChar >= '0' && hexChar <= '9') {
      return hexChar - '0';
    } else if (hexChar >= 'A' && hexChar <= 'F') {
      return hexChar - 'A' + 10;
    } else {
      throw new IllegalArgumentException();
    }
  }

  private static byte[] base64Encode(final byte[] input) {
    return Base64.encode(input, Base64.NO_WRAP);
  }

  private static byte[] base64Decode(final byte[] input) {
    return Base64.decode(input, Base64.NO_WRAP);
  }

  private static boolean isSpace(final String s) {
    if (s == null) {
      return true;
    }
    for (int i = 0, len = s.length(); i < len; ++i) {
      if (!Character.isWhitespace(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
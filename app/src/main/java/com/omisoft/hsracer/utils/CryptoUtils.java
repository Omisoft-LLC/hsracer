package com.omisoft.hsracer.utils;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPrivateKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Digest utils Created by dido on 02.02.17.
 */

public class CryptoUtils {


  private static final String TAG = CryptoUtils.class.getName();
  private static final int IV_SIZE = 16;
  private static final int BLOCK_BITS = 256;
  private static final String PK_ENCRYPT_ALG = "RSA/NONE/OAEPWithSHA512AndMGF1Padding";


  public static void saveKeyToFile(File file,
      BigInteger mod, BigInteger exp) {
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(
          new BufferedOutputStream(new FileOutputStream(file)));

      out.writeObject(mod);
      out.writeObject(exp);
    } catch (Exception e) {
      throw new SecurityException(e);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static PrivateKey readPrivateKey(File file) {
    ObjectInputStream oin = null;
    try {
      InputStream in = new FileInputStream(file);

      oin = new ObjectInputStream(new BufferedInputStream(in));
      BigInteger m = (BigInteger) oin.readObject();
      BigInteger e = (BigInteger) oin.readObject();
      RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
      KeyFactory fact = KeyFactory.getInstance("RSA");
      return fact.generatePrivate(keySpec);
    } catch (Exception e) {
      throw new SecurityException(e);
    } finally {
      try {
        oin.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Generate AES key
   */
  public static SecretKey constructAesKey()
      throws NoSuchProviderException, NoSuchAlgorithmException {
    KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
    aesKeyGenerator.init(BLOCK_BITS);
    SecretKey encryptionKey = aesKeyGenerator.generateKey();
    encryptionKey.getEncoded();
    return encryptionKey;
  }


  public static byte[] encrypt(SecretKey key, final byte[] message)
      throws SecurityException {
    SecureRandom random = new SecureRandom();
    byte[] iv = new byte[IV_SIZE];
    random.nextBytes(iv);
    // log.info("encrypt iv:" + Arrays.toString(iv));
    // log.info("ENCRYPT KEY:" + keyType.name() + ":" + Arrays.toString(key.getEncoded()));

    Cipher cipher;
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
      byte[] encryptedMessage = cipher.doFinal(message);
      byte[] concatenetad = new byte[encryptedMessage.length + IV_SIZE];
      System.arraycopy(iv, 0, concatenetad, 0, IV_SIZE);
      System.arraycopy(encryptedMessage, 0, concatenetad, IV_SIZE, encryptedMessage.length);

      return concatenetad;
    } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException
        | InvalidAlgorithmParameterException | BadPaddingException | NoSuchPaddingException e) {
      throw new SecurityException(e);
    }

  }

  /**
   * Decrypt message
   */
  public static byte[] decrypt(SecretKey key, final byte[] message)
      throws SecurityException {

    Cipher cipher;
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      byte[] iv = new byte[IV_SIZE];
      byte[] originalEncryptedMessage = new byte[message.length - IV_SIZE];

      System.arraycopy(message, 0, iv, 0, IV_SIZE);
      System.arraycopy(message, IV_SIZE, originalEncryptedMessage, 0,
          originalEncryptedMessage.length);

      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
      return cipher.doFinal(originalEncryptedMessage);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException
        | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
      throw new SecurityException(e);
    }
  }

  public static String convertToHex(byte[] data) {
    StringBuilder buf = new StringBuilder();
    for (byte b : data) {
      int halfbyte = (b >>> 4) & 0x0F;
      int two_halfs = 0;
      do {
        buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte)
            : (char) ('a' + (halfbyte - 10)));
        halfbyte = b & 0x0F;
      } while (two_halfs++ < 1);
    }
    return buf.toString();
  }

  /**
   * Decrypt byte array with private key stored in keystore
   */
  public static byte[] decryptWithPrivateKey(PrivateKey privateKey, byte[] encryptedMessage)
      throws SecurityException {
    byte[] decryptedMessage = null;
    String testString = new String(encryptedMessage, Charset.forName("UTF-8"));
    //Log.d(TAG,testString);
    try {
      Cipher cypher = Cipher.getInstance(PK_ENCRYPT_ALG);
      cypher.init(Cipher.DECRYPT_MODE, privateKey);
      decryptedMessage = cypher.doFinal(encryptedMessage);
    } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException
        | NoSuchPaddingException | InvalidKeyException e) {
      Log.e(TAG, Log.getStackTraceString(e));
    }
    return decryptedMessage;
  }

  /**
   * Encrypt with public key
   */
  public static byte[] encryptWithPublicKey(PublicKey publicKey, byte[] messageToEncrypt)
      throws SecurityException {
    try {
      Cipher cypher = Cipher.getInstance(PK_ENCRYPT_ALG);

      cypher.init(Cipher.ENCRYPT_MODE, publicKey);
      return cypher.doFinal(messageToEncrypt);
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException
        | NoSuchAlgorithmException | NoSuchPaddingException e) {
      Log.e(TAG, Log.getStackTraceString(e));
      throw new SecurityException(e);

    }


  }


}

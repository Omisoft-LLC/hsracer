package com.omisoft.hsracer.common.structures;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Secure String implementation Created by develope on 08/12/16.
 */

@JsonDeserialize(using = SecureStringDeserializer.class)
@JsonSerialize(using = SecureStringSerializer.class)
public class SecureString implements CharSequence, Parcelable {

  public static final Creator<SecureString> CREATOR = new Creator<SecureString>() {
    @Override
    public SecureString createFromParcel(Parcel in) {
      return new SecureString(in);
    }

    @Override
    public SecureString[] newArray(int size) {
      return new SecureString[size];
    }
  };
  private final char[] chars;

  public SecureString(char[] chars) {
    this.chars = new char[chars.length];
    System.arraycopy(chars, 0, this.chars, 0, chars.length);

  }

  public SecureString(char[] chars, int start, int end) {
    this.chars = new char[end - start];
    System.arraycopy(chars, start, this.chars, 0, this.chars.length);
  }

  protected SecureString(Parcel in) {
    chars = in.createCharArray();
  }

  public SecureString(byte[] bytes) {

    this(new String(bytes, Charset.forName("UTF-8")).toCharArray());


  }

  /**
   * This constructor is insecure
   */
  public SecureString(String string) {
    this(string.toCharArray());
  }

  @Override
  public int length() {
    return chars.length;
  }

  @Override
  public char charAt(int index) {
    return chars[index];
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return new SecureString(this.chars, start, end);
  }

  /**
   * MUST manually clear the underlying array holding the characters
   */
  public void clear() {
    Arrays.fill(chars, '0');
  }

  /**
   * Returns copy of char array (use only in secure objects)
   */
  public char[] toCharArray() {
    return Arrays.copyOf(this.chars, this.length());
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(chars);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SecureString that = (SecureString) o;

    return Arrays.equals(chars, that.chars);

  }

  @Override
  @NonNull
  public String toString() {
    return new String(chars);
  }

  @Override
  public void finalize() throws Throwable {
    clear();
    super.finalize();
  }

  public char codeAt(int i) {
    return chars[i];
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeCharArray(chars);
  }

  /**
   * Convert secure string to byte array. Secure string is utf-8
   */
  public byte[] toBytes() {
    CharBuffer charBuffer = CharBuffer.wrap(chars);
    ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
    byte[] bytes =
        Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
    Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
    Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
    return bytes;
  }

  public SecureString concat(SecureString strToAdd) {
    int thisLength = this.length();
    int thatLength = strToAdd.length();
    char[] result = new char[thisLength + thatLength];
    System.arraycopy(this.chars, 0, result, 0, thisLength);
    System.arraycopy(strToAdd.chars, 0, result, thisLength, thatLength);
    return new SecureString(result);
  }


}


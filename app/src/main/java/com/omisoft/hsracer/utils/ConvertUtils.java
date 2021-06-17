package com.omisoft.hsracer.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Performs various conversions of different units/types
 */
public final class ConvertUtils {

  private ConvertUtils() {
    throw new UnsupportedOperationException(
        Utils.NO_INIT
    );
  }

  private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
      'B', 'C', 'D', 'E', 'F'};

  /**
   * Bytes to hex string
   */
  public static String bytes2HexString(final byte[] bytes) {
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

  /**
   * Hex string to bytes
   */
  public static byte[] hexString2Bytes(String hexString) {
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

  /**
   * hex to decimal
   */
  private static int hex2Dec(final char hexChar) {
    if (hexChar >= '0' && hexChar <= '9') {
      return hexChar - '0';
    } else if (hexChar >= 'A' && hexChar <= 'F') {
      return hexChar - 'A' + 10;
    } else {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Convert chars to bytes
   */
  public static byte[] chars2Bytes(final char[] chars) {
    if (chars == null || chars.length <= 0) {
      return null;
    }
    int len = chars.length;
    byte[] bytes = new byte[len];
    for (int i = 0; i < len; i++) {
      bytes[i] = (byte) (chars[i]);
    }
    return bytes;
  }

  /**
   * Convert bytes to chars
   */
  public static char[] bytes2Chars(final byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    int len = bytes.length;
    if (len <= 0) {
      return null;
    }
    char[] chars = new char[len];
    for (int i = 0; i < len; i++) {
      chars[i] = (char) (bytes[i] & 0xff);
    }
    return chars;
  }

  /**
   * Returns memory size in bytes
   */
  public static long memorySize2Byte(final long memorySize, @MemoryConstants.Unit final int unit) {
    if (memorySize < 0) {
      return -1;
    }
    return memorySize * unit;
  }

  /**
   * Returns memory size in unit
   */
  public static double byte2MemorySize(final long byteNum, @MemoryConstants.Unit final int unit) {
    if (byteNum < 0) {
      return -1;
    }
    return (double) byteNum / unit;
  }

  /**
   * The number of bytes to the appropriate memory size
   *      <P> keep 3 decimal places </ p>
   *    
   *      @param byteNum bytes
   *      @return appropriate memory size
   */
  @SuppressLint("DefaultLocale")
  public static String byte2FitMemorySize(final long byteNum) {
    if (byteNum < 0) {
      return "shouldn't be less than zero!";
    } else if (byteNum < MemoryConstants.KB) {
      return String.format("%.3fB", (double) byteNum + 0.0005);
    } else if (byteNum < MemoryConstants.MB) {
      return String.format("%.3fKB", (double) byteNum / MemoryConstants.KB + 0.0005);
    } else if (byteNum < MemoryConstants.GB) {
      return String.format("%.3fMB", (double) byteNum / MemoryConstants.MB + 0.0005);
    } else {
      return String.format("%.3fGB", (double) byteNum / MemoryConstants.GB + 0.0005);
    }
  }

  /**
   * Turns the timestamp in units of time    
   *
   * @param timeSpan millisecond timestamp      
   * @return millisecond timestamp
   */
  public static long timeSpan2Millis(final long timeSpan, @TimeConstants.Unit final int unit) {
    return timeSpan * unit;
  }


  /**
   * Mills in time unit
   */
  public static long millis2TimeSpan(final long millis, @TimeConstants.Unit final int unit) {
    return millis / unit;
  }

  /**
   * Millisecond timestamp to fit the length of time     *     * @param millis millisecond timestamp
   * <p> less than or equal to 0, return to null </ p>     * @param precision precision <ul> <li>
   * precision = 0, return null </ li> <li> precision = 1, return day </ li> <li> precision     * =
   * 2, return days and hours </ li> <li> precision = 3, return days, hours and minutes </ li> <li>
   * precision = 4, return days, hours, minutes and seconds </ li>     * <Li> precision & gt; = 5,
   * return days, hours, minutes, seconds and milliseconds </ li> </ ul>     * @return appropriate
   * length of time
   */
  @SuppressLint("DefaultLocale")
  public static String millis2FitTimeSpan(long millis, int precision) {
    if (millis <= 0 || precision <= 0) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
    int[] unitLen = {86400000, 3600000, 60000, 1000, 1};
    precision = Math.min(precision, 5);
    for (int i = 0; i < precision; i++) {
      if (millis >= unitLen[i]) {
        long mode = millis / unitLen[i];
        millis -= mode * unitLen[i];
        sb.append(mode).append(units[i]);
      }
    }
    return sb.toString();
  }

  /**
   * bytes to bits
   *
   * @return bits
   */
  public static String bytes2Bits(final byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte aByte : bytes) {
      for (int j = 7; j >= 0; --j) {
        sb.append(((aByte >> j) & 0x01) == 0 ? '0' : '1');
      }
    }
    return sb.toString();
  }

  /**
   * bits to bytes
   */
  public static byte[] bits2Bytes(String bits) {
    int lenMod = bits.length() % 8;
    int byteLen = bits.length() / 8;
    // 不是8的倍数前面补0
    if (lenMod != 0) {
      for (int i = lenMod; i < 8; i++) {
        bits = "0" + bits;
      }
      byteLen++;
    }
    byte[] bytes = new byte[byteLen];
    for (int i = 0; i < byteLen; ++i) {
      for (int j = 0; j < 8; ++j) {
        bytes[i] <<= 1;
        bytes[i] |= bits.charAt(i * 8 + j) - '0';
      }
    }
    return bytes;
  }

  /**
   * Copy unit stream to byteoutputstream
   */
  public static ByteArrayOutputStream input2OutputStream(final InputStream is) {
    if (is == null) {
      return null;
    }
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      byte[] b = new byte[MemoryConstants.KB];
      int len;
      while ((len = is.read(b, 0, MemoryConstants.KB)) != -1) {
        os.write(b, 0, len);
      }
      return os;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      CloseUtils.closeIO(is);
    }
  }

  /**
   * outputStream to inputStream
   */
  public ByteArrayInputStream output2InputStream(final OutputStream out) {
    if (out == null) {
      return null;
    }
    return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
  }

  /**
   * inputStream to byteArr
   */
  public static byte[] inputStream2Bytes(final InputStream is) {
    if (is == null) {
      return null;
    }
    return input2OutputStream(is).toByteArray();
  }

  /**
   * byteArr byte array to inputStream
   */
  public static InputStream bytes2InputStream(final byte[] bytes) {
    if (bytes == null || bytes.length <= 0) {
      return null;
    }
    return new ByteArrayInputStream(bytes);
  }

  /**
   * outputStream to byteArr
   */
  public static byte[] outputStream2Bytes(final OutputStream out) {
    if (out == null) {
      return null;
    }
    return ((ByteArrayOutputStream) out).toByteArray();
  }

  /**
   * outputStream to byteArr
   */
  public static OutputStream bytes2OutputStream(final byte[] bytes) {
    if (bytes == null || bytes.length <= 0) {
      return null;
    }
    ByteArrayOutputStream os = null;
    try {
      os = new ByteArrayOutputStream();
      os.write(bytes);
      return os;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      CloseUtils.closeIO(os);
    }
  }

  /**
   * input Stream to string
   */
  public static String inputStream2String(final InputStream is, final String charsetName) {
    if (is == null || isSpace(charsetName)) {
      return null;
    }
    try {
      return new String(inputStream2Bytes(is), charsetName);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * string to inputStream
   *
   * @return 输入流
   */
  public static InputStream string2InputStream(final String string, final String charsetName) {
    if (string == null || isSpace(charsetName)) {
      return null;
    }
    try {
      return new ByteArrayInputStream(string.getBytes(charsetName));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * outputStream to string
   */
  public static String outputStream2String(final OutputStream out, final String charsetName) {
    if (out == null || isSpace(charsetName)) {
      return null;
    }
    try {
      return new String(outputStream2Bytes(out), charsetName);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * string string to outputStream
   *
   * @param string 字符串
   * @param charsetName 编码格式
   * @return 输入流
   */
  public static OutputStream string2OutputStream(final String string, final String charsetName) {
    if (string == null || isSpace(charsetName)) {
      return null;
    }
    try {
      return bytes2OutputStream(string.getBytes(charsetName));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * bitmap to byteArr
   *
   * @param bitmap bitmap
   */
  public static byte[] bitmap2Bytes(final Bitmap bitmap, final Bitmap.CompressFormat format) {
    if (bitmap == null) {
      return null;
    }
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    bitmap.compress(format, 100, byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }

  /**
   * byteArr to bitmap
   *
   * @return bitmap
   */
  public static Bitmap bytes2Bitmap(final byte[] bytes) {
    return (bytes == null || bytes.length == 0) ? null
        : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
  }

  /**
   * drawable to bitmap
   *
   * @param drawable drawable
   * @return bitmap
   */
  public static Bitmap drawable2Bitmap(final Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
      if (bitmapDrawable.getBitmap() != null) {
        return bitmapDrawable.getBitmap();
      }
    }
    Bitmap bitmap;
    if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
      bitmap = Bitmap.createBitmap(1, 1,
          drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
              : Bitmap.Config.RGB_565);
    } else {
      bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
          drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
              : Bitmap.Config.RGB_565);
    }
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
  }

  /**
   * bitmap to drawable
   *
   * @param bitmap bitmap
   * @return drawable
   */
  public static Drawable bitmap2Drawable(final Bitmap bitmap) {
    return bitmap == null ? null : new BitmapDrawable(Utils.getApp().getResources(), bitmap);
  }

  /**
   * drawable to byteArr
   *
   * @param drawable drawable
   */
  public static byte[] drawable2Bytes(final Drawable drawable, final Bitmap.CompressFormat format) {
    return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable), format);
  }

  /**
   * byteArr drawable
   *
   * @return drawable
   */
  public static Drawable bytes2Drawable(final byte[] bytes) {
    return bytes == null ? null : bitmap2Drawable(bytes2Bitmap(bytes));
  }

  /**
   * view to Bitmap
   *
   * @return bitmap
   */
  public static Bitmap view2Bitmap(final View view) {
    if (view == null) {
      return null;
    }
    Bitmap ret = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(ret);
    Drawable bgDrawable = view.getBackground();
    if (bgDrawable != null) {
      bgDrawable.draw(canvas);
    } else {
      canvas.drawColor(Color.WHITE);
    }
    view.draw(canvas);
    return ret;
  }

  /**
   * dp to px
   *
   * @param dpValue dp
   * @return px
   */
  public static int dp2px(final float dpValue) {
    final float scale = Utils.getApp().getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * px to dp
   *
   * @return dp
   */
  public static int px2dp(final float pxValue) {
    final float scale = Utils.getApp().getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  /**
   * sp to px
   *
   * @param spValue sp
   * @return px
   */
  public static int sp2px(final float spValue) {
    final float fontScale = Utils.getApp().getResources().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }

  /**
   * px to sp
   *
   * @param pxValue px
   * @return sp
   */
  public static int px2sp(final float pxValue) {
    final float fontScale = Utils.getApp().getResources().getDisplayMetrics().scaledDensity;
    return (int) (pxValue / fontScale + 0.5f);
  }

  /**
   * Check if is space
   *
   * @return {@code true}: or <br> {@code false}:
   */
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
package com.omisoft.hsracer.utils;

import android.os.Build;
import android.text.Html;
import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Encode Utils (URL & Base64)
 */
public final class EncodeUtils {

  private EncodeUtils() {
    throw new UnsupportedOperationException(Utils.NO_INIT);
  }

  /**
   * URL encode
   */
  public static String urlEncode(final String input) {
    return encode(input);
  }

  /**
   * URL encode
   */
  public static String encode(final String input) {
    try {
      return URLEncoder.encode(input, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return input;
    }
  }

  /**
   * URL decode
   */
  public static String urlDecode(final String input) {
    return decode(input);
  }

  /**
   * URL decode
   */
  public static String decode(final String input) {
    try {
      return URLDecoder.decode(input, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return input;
    }
  }

  /**
   * Base64 Encode
   */
  public static byte[] base64Encode(final String input) {
    return base64Encode(input.getBytes());
  }

  /**
   * Base64 Encode
   */
  public static byte[] base64Encode(final byte[] input) {
    return Base64.encode(input, Base64.NO_WRAP);
  }

  /**
   * Base64 to String
   */
  public static String base64Encode2String(final byte[] input) {
    return Base64.encodeToString(input, Base64.NO_WRAP);
  }

  /**
   * Base64 decode
   */
  public static byte[] base64Decode(final String input) {
    return Base64.decode(input, Base64.NO_WRAP);
  }

  /**
   * Base64 decode
   */
  public static byte[] base64Decode(final byte[] input) {
    return Base64.decode(input, Base64.NO_WRAP);
  }

  /**
   * Base64URL safe encode
   */
  public static byte[] base64UrlSafeEncode(final String input) {
    return Base64.encode(input.getBytes(), Base64.URL_SAFE);
  }

  /**
   * Html encode
   */
  public static String htmlEncode(final CharSequence input) {
    StringBuilder sb = new StringBuilder();
    char c;
    for (int i = 0, len = input.length(); i < len; i++) {
      c = input.charAt(i);
      switch (c) {
        case '<':
          sb.append("&lt;"); //$NON-NLS-1$
          break;
        case '>':
          sb.append("&gt;"); //$NON-NLS-1$
          break;
        case '&':
          sb.append("&amp;"); //$NON-NLS-1$
          break;
        case '\'':
          //http://www.w3.org/TR/xhtml1
          // The named character reference &apos; (the apostrophe, U+0027) was
          // introduced in XML 1.0 but does not appear in HTML. Authors should
          // therefore use &#39; instead of &apos; to work as expected in HTML 4
          // user agents.
          sb.append("&#39;"); //$NON-NLS-1$
          break;
        case '"':
          sb.append("&quot;"); //$NON-NLS-1$
          break;
        default:
          sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * Html decode
   */
  @SuppressWarnings("deprecation")
  public static CharSequence htmlDecode(final String input) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY);
    } else {
      return Html.fromHtml(input);
    }
  }
}
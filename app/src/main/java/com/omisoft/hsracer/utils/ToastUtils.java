package com.omisoft.hsracer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.TextViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.ref.WeakReference;

/**
 * Various toast activities
 */
public final class ToastUtils {

  private static final int DEFAULT_COLOR = 0xFEFFFFFF;
  private static final Handler HANDLER = new Handler(Looper.getMainLooper());
  private static Toast sToast;
  private static WeakReference<View> sViewWeakReference;
  private static int gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
  private static int xOffset = 0;
  private static int yOffset = (int) (64 * Utils.getApp().getResources().getDisplayMetrics().density
      + 0.5);
  private static int backgroundColor = DEFAULT_COLOR;
  private static int bgResource = -1;
  private static int messageColor = DEFAULT_COLOR;

  private ToastUtils() {
    throw new UnsupportedOperationException(Utils.NO_INIT);
  }

  /**
   * set gravity
   */
  public static void setGravity(final int gravity, final int xOffset, final int yOffset) {
    ToastUtils.gravity = gravity;
    ToastUtils.xOffset = xOffset;
    ToastUtils.yOffset = yOffset;
  }

  /**
   * set view
   */
  public static void setView(@LayoutRes final int layoutId) {
    LayoutInflater inflate = (LayoutInflater) Utils.getApp()
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    sViewWeakReference = new WeakReference<>(inflate.inflate(layoutId, null));
  }

  /**
   * set view
   */
  public static void setView(final View view) {
    sViewWeakReference = view == null ? null : new WeakReference<>(view);
  }

  /**
   * get view
   *
   * @return view
   */
  public static View getView() {
    final View view = getViewFromWR();
    if (view != null) {
      return view;
    }
    if (sToast != null) {
      return sToast.getView();
    }
    return null;
  }

  /**
   * change Background color
   */
  public static void setBgColor(@ColorInt final int backgroundColor) {
    ToastUtils.backgroundColor = backgroundColor;
  }

  /**
   * Change background resource
   */
  public static void setBgResource(@DrawableRes final int bgResource) {
    ToastUtils.bgResource = bgResource;
  }

  /**
   * Change message color
   */
  public static void setMessageColor(@ColorInt final int messageColor) {
    ToastUtils.messageColor = messageColor;
  }

  /**
   * Show short toast
   */
  public static void showShortSafe(@NonNull final CharSequence text) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        show(text, Toast.LENGTH_SHORT);
      }
    });
  }

  /**
   * Show short toast
   */
  public static void showShortSafe(@StringRes final int resId) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        show(resId, Toast.LENGTH_SHORT);
      }
    });
  }

  /**
   * Show short toast
   */
  public static void showShortSafe(@StringRes final int resId, final Object... args) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        show(resId, Toast.LENGTH_SHORT, args);
      }
    });
  }

  /**
   * Show short toast
   */
  public static void showShortSafe(final String format, final Object... args) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        show(format, Toast.LENGTH_SHORT, args);
      }
    });
  }

  /**
   * Show long toast
   */
  public static void showLongSafe(@NonNull final CharSequence text) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        show(text, Toast.LENGTH_LONG);
      }
    });
  }

  /**
   * Show long toast
   */
  public static void showLongSafe(@StringRes final int resId) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        show(resId, Toast.LENGTH_LONG);
      }
    });
  }

  /**
   * Show long toast
   */
  public static void showLongSafe(@StringRes final int resId, final Object... args) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        show(resId, Toast.LENGTH_LONG, args);
      }
    });
  }

  /**
   * Show long toast
   */
  public static void showLongSafe(final String format, final Object... args) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        show(format, Toast.LENGTH_LONG, args);
      }
    });
  }

  /**
   * Show short
   */
  public static void showShort(@NonNull final CharSequence text) {
    show(text, Toast.LENGTH_SHORT);
  }

  /**
   * Show short
   */
  public static void showShort(@StringRes final int resId) {
    show(resId, Toast.LENGTH_SHORT);
  }

  /**
   * Show short
   */
  public static void showShort(@StringRes final int resId, final Object... args) {
    show(resId, Toast.LENGTH_SHORT, args);
  }

  /**
   * Show short
   */
  public static void showShort(final String format, final Object... args) {
    show(format, Toast.LENGTH_SHORT, args);
  }

  /**
   * Show long
   */
  public static void showLong(@NonNull final CharSequence text) {
    show(text, Toast.LENGTH_LONG);
  }

  /**
   * Show long
   */
  public static void showLong(@StringRes final int resId) {
    show(resId, Toast.LENGTH_LONG);
  }

  /**
   * Show long
   */
  public static void showLong(@StringRes final int resId, final Object... args) {
    show(resId, Toast.LENGTH_LONG, args);
  }

  /**
   * Show long
   */
  public static void showLong(final String format, final Object... args) {
    show(format, Toast.LENGTH_LONG, args);
  }

  /**
   * Show custom toast
   */
  public static void showCustomShortSafe(@LayoutRes final int layoutId) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        setView(layoutId);
        show("", Toast.LENGTH_SHORT);
      }
    });
  }

  /**
   * Show custom toast
   */
  public static void showCustomLongSafe(@LayoutRes final int layoutId) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        setView(layoutId);
        show("", Toast.LENGTH_LONG);
      }
    });
  }

  /**
   * Show custom toast
   */
  public static void showCustomShort(@LayoutRes final int layoutId) {
    setView(layoutId);
    show("", Toast.LENGTH_SHORT);
  }

  /**
   * Show custom toast
   */
  public static void showCustomLong(@LayoutRes final int layoutId) {
    setView(layoutId);
    show("", Toast.LENGTH_LONG);
  }

  /**
   * Show custom toast
   */
  public static void showCustomShortSafe(@NonNull final View view) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        setView(view);
        show("", Toast.LENGTH_SHORT);
      }
    });
  }

  /**
   * Show custom toast
   */
  public static void showCustomLongSafe(@NonNull final View view) {
    HANDLER.post(new Runnable() {
      @Override
      public void run() {
        setView(view);
        show("", Toast.LENGTH_LONG);
      }
    });
  }

  /**
   * Show custom toast
   */
  public static void showCustomShort(@NonNull final View view) {
    setView(view);
    show("", Toast.LENGTH_SHORT);
  }

  /**
   * Show custom toast
   */
  public static void showCustomLong(@NonNull final View view) {
    setView(view);
    show("", Toast.LENGTH_LONG);
  }

  /**
   * Actual show
   */
  private static void show(@StringRes final int resId, final int duration) {
    show(Utils.getApp().getResources().getText(resId).toString(), duration);
  }

  /**
   * Actual show
   */
  private static void show(@StringRes final int resId, final int duration, final Object... args) {
    show(String.format(Utils.getApp().getResources().getString(resId), args), duration);
  }

  /**
   * Actual show
   */
  private static void show(final String format, final int duration, final Object... args) {
    show(String.format(format, args), duration);
  }

  /**
   * Actual show
   */
  @SuppressLint("ShowToast")
  private static void show(final CharSequence text, final int duration) {
    cancel();
    final View view = getViewFromWR();
    if (view != null) {
      sToast = new Toast(Utils.getApp());
      sToast.setView(view);
      sToast.setDuration(duration);
    } else {
      sToast = Toast.makeText(Utils.getApp(), text, duration);
      // solve the font of toast
      TextView tvMessage = sToast.getView().findViewById(android.R.id.message);
      TextViewCompat.setTextAppearance(tvMessage, android.R.style.TextAppearance);
      tvMessage.setTextColor(messageColor);
    }
    View toastView = sToast.getView();
    if (bgResource != -1) {
      toastView.setBackgroundResource(bgResource);
    } else if (backgroundColor != DEFAULT_COLOR) {
      toastView.setBackgroundColor(backgroundColor);
    }
    sToast.setGravity(gravity, xOffset, yOffset);
    sToast.show();
  }

  /**
   * Cancel toast
   */
  public static void cancel() {
    if (sToast != null) {
      sToast.cancel();
      sToast = null;
    }
  }

  private static View getViewFromWR() {
    if (sViewWeakReference != null) {
      final View view = sViewWeakReference.get();
      if (view != null) {
        return view;
      }
    }
    return null;
  }
}
/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omisoft.hsracer.utils;

import static com.omisoft.hsracer.constants.Constants.ONE_HOUR;
import static com.omisoft.hsracer.constants.Constants.ONE_MINUTE;
import static com.omisoft.hsracer.constants.Constants.ONE_SECOND;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.model.Car;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * This provides methods to help Activities load their UI.
 */
public class AndroidUtils {

  /**
   * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
   * performed by the {@code fragmentManager}.
   */
  public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
      @NonNull Fragment fragment, int frameId) {

    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.add(frameId, fragment);
    transaction.commit();
  }

  public static boolean isEmailValid(String email) {
    return !Pattern.matches(Constants.EMAIL_REGEX, email);
  }

  public static boolean isSDCardAvailable() {
    if (android.os.Environment.getExternalStorageState()
        .equals(android.os.Environment.MEDIA_MOUNTED)) {
      return !Environment.isExternalStorageEmulated();
    }
    return false;
  }

  public static boolean isEmulator() {
    return Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown")
        || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for x86")
        || Build.MANUFACTURER.contains("Genymotion")
        || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        || "google_sdk" .equals(Build.PRODUCT);
  }

  public static String[] getCarsArray(List<Car> cars) {
    String[] carsArray;
    if (cars != null) {
      List<String> makeModel = new ArrayList<>();
      for (Car car : cars) {
        makeModel.add(car.getManufacturer() + " " + car.getModel());
      }
      carsArray = makeModel.toArray(new String[makeModel.size()]);
    } else {
      carsArray = new String[]{"No cars added"};
    }
    return carsArray;
  }

  /**
   * Convert from m to ft
   */
  public static int fromMeterToFeet(int value) {
    return (int) (value * 3.28084);
  }

  /**
   * Convert from ft to m
   */
  public static int fromFeetToMeter(int value) {
    return (int) (value * 0.3048);
  }

  /**
   * Converts from km to miles
   */
  public static int fromKilometresToMiles(int value) {

    return (int) (value * 0.621371);
  }

  /**
   * Converts from miles to km
   */
  public static int fromMilesToKilometres(int value) {

    return (int) (value * 1.60934);
  }

  /**
   * Converts from Celsius to Fahrenheit
   */
  public static int fromCelsiusToFahrenheit(int value) {

    return (int) ((value * 1.8) + 32);
  }

  /**
   * Formats double values
   */
  public static String formatDouble(double d) {
    if (d == (long) d) {
      return String.format(Locale.getDefault(), "%d", (long) d);
    } else {
      return String.format(Locale.getDefault(), "%s", d);
    }
  }

  /**
   * Check if camera is used
   */
  public static boolean isCameraUsedByApp() {
    Log.wtf("CAMERA", "isCameraUsedByApp: ");
    Camera camera = null;
    try {
      camera = Camera.open();
    } catch (RuntimeException e) {
      return true;
    } finally {
      if (camera != null) {
        camera.release();
      }
    }
    return false;
  }


  /**
   * Calculates video size in bytes for seconds
   */
  public static long calculateVideoSizeBasedOnProfile(CamcorderProfile profile, int seconds) {
    return (long) (((profile.videoBitRate + profile.audioBitRate) / 8) * seconds);
  }

  /**
   * Get megabytes left for file
   */
  @SuppressLint("ObsoleteSdkInt")
  public static float megabytesAvailable(File f) {
    StatFs stat = new StatFs(f.getPath());
    long bytesAvailable = 0;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
      bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
    } else {
      bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
    }
    return bytesAvailable / (1024.f * 1024.f);
  }

  public static boolean isOBDReadReady(SharedPreferences prefs) {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    boolean pairedDevice = false;
    if (!TextUtils.isEmpty(prefs.getString(Constants.OBD_DEVICE_KEY, ""))) {
      pairedDevice = true;
    }
    boolean isOBDEnabled = prefs.getBoolean(Constants.OBD_ENABLE_KEY, false);
    return isOBDEnabled && pairedDevice && bluetoothAdapter.isEnabled();
  }


  /**
   * Parse a long attribute which shows the time in the race
   */
  public static String formatTimeRacing(Long time) {
    int h = (int) (time / ONE_HOUR);
    int m = (int) (time - h * ONE_HOUR) / ONE_MINUTE;
    int s = (int) (time - h * ONE_HOUR - m * ONE_MINUTE) / ONE_SECOND;
    return String.format("%s:%s:%s", (h < 10 ? "0" + h : h),
        (m < 10 ? "0" + m : m), (s < 10 ? "0" + s
            : s));
  }

  /**
   * @param view View to animate
   * @param toVisibility Visibility at the end of animation
   * @param toAlpha Alpha at the end of animation
   * @param duration Animation duration in ms
   */
  public static void animateView(final View view, final int toVisibility, float toAlpha,
      int duration) {
    boolean show = toVisibility == View.VISIBLE;
    if (show) {
      view.setAlpha(0);
    }
    view.setVisibility(View.VISIBLE);
    view.animate()
        .setDuration(duration)
        .alpha(show ? toAlpha : 0)
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            view.setVisibility(toVisibility);
          }
        });
  }

  /**
   * Copy parceable
   */
  public static <T extends Parcelable> T copy(T original) {
    Parcel p = Parcel.obtain();
    original.writeToParcel(p, 0);
    p.setDataPosition(0);
    T copy = null;
    try {
      copy = (T) original.getClass().getDeclaredConstructor(new Class[]{Parcel.class})
          .newInstance(p);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return copy;
  }

  public static boolean isMockSettingsON(Context context) {
    // returns true if mock location enabled, false if not enabled.
    return !Settings.Secure.getString(context.getContentResolver(),
        Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
  }

  public static boolean areThereMockPermissionApps(Context context) {
    int count = 0;

    PackageManager pm = context.getPackageManager();
    List<ApplicationInfo> packages =
        pm.getInstalledApplications(PackageManager.GET_META_DATA);

    for (ApplicationInfo applicationInfo : packages) {
      try {
        PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
            PackageManager.GET_PERMISSIONS);

        // Get Permissions
        String[] requestedPermissions = packageInfo.requestedPermissions;

        if (requestedPermissions != null) {
          for (String requestedPermission : requestedPermissions) {
            if (requestedPermission
                .equals("android.permission.ACCESS_MOCK_LOCATION")
                && !applicationInfo.packageName.equals(context.getPackageName())) {
              count++;
            }
          }
        }
      } catch (NameNotFoundException e) {
        Log.e("Got exception ", e.getMessage());
      }
    }

    return count > 0;
  }

  /**
   * Gets the SHA1 signature, hex encoded for inclusion with Google Cloud Platform API requests
   *
   * @param packageName Identifies the APK whose signature should be extracted.
   * @return a lowercase, hex-encoded
   */
  public static String getSignature(@NonNull PackageManager pm, @NonNull String packageName) {
    try {
      PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
      if (packageInfo == null
          || packageInfo.signatures == null
          || packageInfo.signatures.length == 0
          || packageInfo.signatures[0] == null) {
        return null;
      }
      return signatureDigest(packageInfo.signatures[0]);
    } catch (PackageManager.NameNotFoundException e) {
      return null;
    }
  }

  public static String signatureDigest(Signature sig) {
    byte[] signature = sig.toByteArray();
    try {
      MessageDigest md = MessageDigest.getInstance("SHA1");
      byte[] digest = md.digest(signature);
      return CryptoUtils.convertToHex(digest);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }

  public static Uri resourceToUri(Context context, int resID) {
    System.out.println(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
        context.getResources().getResourcePackageName(resID) + '/' +
        context.getResources().getResourceTypeName(resID) + '/' +
        context.getResources().getResourceEntryName(resID));
    return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
        context.getResources().getResourcePackageName(resID) + '/' +
        context.getResources().getResourceTypeName(resID) + '/' +
        context.getResources().getResourceEntryName(resID));
  }
}
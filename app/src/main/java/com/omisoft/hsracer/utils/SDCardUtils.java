package com.omisoft.hsracer.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * SDCard Utils
 */
public final class SDCardUtils {

  private SDCardUtils() {
    throw new UnsupportedOperationException(Utils.NO_INIT);
  }

  /**
   * SD Card
   */
  public static boolean isSDCardEnable() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }

  /**
   * Get path to sd card (mount point)
   */
  public static String getSDCardPath() {
    if (!isSDCardEnable()) {
      return null;
    }
    String cmd = "cat /proc/mounts";
    Runtime run = Runtime.getRuntime();
    BufferedReader bufferedReader = null;
    try {
      Process p = run.exec(cmd);
      bufferedReader = new BufferedReader(
          new InputStreamReader(new BufferedInputStream(p.getInputStream())));
      String lineStr;
      while ((lineStr = bufferedReader.readLine()) != null) {
        if (lineStr.contains("sdcard") && lineStr.contains(".android_secure")) {
          String[] strArray = lineStr.split(" ");
          if (strArray.length >= 5) {
            return strArray[1].replace("/.android_secure", "") + File.separator;
          }
        }
        if (p.waitFor() != 0 && p.exitValue() == 1) {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      CloseUtils.closeIO(bufferedReader);
    }
    return Environment.getExternalStorageDirectory().getPath() + File.separator;
  }

  /**
   * sd ard data path
   *
   * @return data path
   */
  public static String getDataPath() {
    if (!isSDCardEnable()) {
      return null;
    }
    return Environment.getExternalStorageDirectory().getPath() + File.separator + "data"
        + File.separator;
  }

  /**
   * Get free space
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  public static String getFreeSpace() {
    if (!isSDCardEnable()) {
      return null;
    }
    StatFs stat = new StatFs(getSDCardPath());
    long blockSize, availableBlocks;
    availableBlocks = stat.getAvailableBlocksLong();
    blockSize = stat.getBlockSizeLong();
    return ConvertUtils.byte2FitMemorySize(availableBlocks * blockSize);
  }

  /**
   * Get sdcard info
   *
   * @return SDCardInfo
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  public static String getSDCardInfo() {
    if (!isSDCardEnable()) {
      return null;
    }
    SDCardInfo sd = new SDCardInfo();
    sd.isExist = true;
    StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
    sd.totalBlocks = sf.getBlockCountLong();
    sd.blockByteSize = sf.getBlockSizeLong();
    sd.availableBlocks = sf.getAvailableBlocksLong();
    sd.availableBytes = sf.getAvailableBytes();
    sd.freeBlocks = sf.getFreeBlocksLong();
    sd.freeBytes = sf.getFreeBytes();
    sd.totalBytes = sf.getTotalBytes();
    return sd.toString();
  }

  public static class SDCardInfo {

    boolean isExist;
    long totalBlocks;
    long freeBlocks;
    long availableBlocks;
    long blockByteSize;
    long totalBytes;
    long freeBytes;
    long availableBytes;

    @Override
    public String toString() {
      return "isExist=" + isExist +
          "\ntotalBlocks=" + totalBlocks +
          "\nfreeBlocks=" + freeBlocks +
          "\navailableBlocks=" + availableBlocks +
          "\nblockByteSize=" + blockByteSize +
          "\ntotalBytes=" + totalBytes +
          "\nfreeBytes=" + freeBytes +
          "\navailableBytes=" + availableBytes;
    }
  }
}
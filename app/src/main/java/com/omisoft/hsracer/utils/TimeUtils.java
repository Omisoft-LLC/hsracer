package com.omisoft.hsracer.utils;

import android.os.SystemClock;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by dido on 04.05.17.
 */

public final class TimeUtils {


  private static final String NTP_SERVER = "time.google.com";
  private static final String NTP_SERVER_2 = "pool.ntp.org";
  private static final int TIMEOUT = 5000;
  private static final int START_OFFSET = 10000;
  private static final DateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
      Locale.getDefault());


  private TimeUtils() {
    throw new UnsupportedOperationException(Utils.NO_INIT);
  }

  /**
   * returns start time synced with NTP and an offset - START_OFFSET
   */
  public static Date getStartTime() {
    SntpClient client = new SntpClient();

    if (client.requestTime(NTP_SERVER, TIMEOUT)) {
    } else if (client.requestTime(NTP_SERVER_2, TIMEOUT)) {
    } else {
      return new Date();
    }
    long now = client.getNtpTime() + SystemClock.elapsedRealtime() -
        client.getNtpTimeReference();
    Date current = new Date(now);
    return new Date(now + START_OFFSET);
  }

  public static String formatInterval(final long l) {
    final long hr = TimeUnit.MILLISECONDS.toHours(l);
    final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
    final long sec = TimeUnit.MILLISECONDS
        .toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
    final long ms = TimeUnit.MILLISECONDS.toMillis(
        l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS
            .toMillis(sec));
    if (sec == 0) {
      return String.format(Locale.US, "%03d", ms);
    } else if (min == 0) {
      return String.format(Locale.US, "%02d.%03d", sec, ms);
    } else if (hr == 0) {
      return String.format(Locale.US, "%02d:%02d.%03d", min, sec, ms);
    } else {
      return String.format(Locale.US, "%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }
  }


  /**
   * Change the timestamp to a time string
   * <P> format is yyyy-MM-dd HH: mm: ss </ p>
   *
   * @param millis millisecond timestamp
   * @return time string
   */
  public static String millis2String(final long millis) {
    return millis2String(millis, DEFAULT_FORMAT);
  }

  /**
   * Change the timestamp to a time string
   * <P> format is format </ p>
   *
   * @param millis millisecond timestamp
   * @param format time format
   * @return time string
   */
  public static String millis2String(final long millis, final DateFormat format) {
    return format.format(new Date(millis));
  }

  /**
   * Turn the time string into a timestamp
   * <P> time format is yyyy-MM-dd HH: mm: ss </ p>
   *
   * @param time time string
   * @return millisecond timestamp
   */
  public static long string2Millis(final String time) {
    return string2Millis(time, DEFAULT_FORMAT);
  }

  /**
   * Turn the time string into a timestamp
   * <P> time format is format </ p>
   *
   * @param time time string
   * @param format time format
   * @return millisecond timestamp
   */
  public static long string2Millis(final String time, final DateFormat format) {
    try {
      return format.parse(time).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * Converts the time string to the Date type
   * <P> time format is yyyy-MM-dd HH: mm: ss </ p>
   *
   * @param time time string
   * @return Date type
   */
  public static Date string2Date(final String time) {
    return string2Date(time, DEFAULT_FORMAT);
  }

  /**
   * Converts the time string to the Date type
   * <P> time format is format </ p>
   *
   * @param time time string
   * @param format time format
   * @return Date type
   */
  public static Date string2Date(final String time, final DateFormat format) {
    try {
      return format.parse(time);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Change the Date type to a time string
   * <P> format is yyyy-MM-dd HH: mm: ss </ p>
   *
   * @param date Date type time
   * @return time string
   */
  public static String date2String(final Date date) {
    return date2String(date, DEFAULT_FORMAT);
  }

  /**
   * Change the Date type to a time string
   * <P> format is format </ p>
   *
   * @param date Date type time
   * @param format time format
   * @return time string
   */
  public static String date2String(final Date date, final DateFormat format) {
    return format.format(date);
  }

  /**
   * Change the Date type to a timestamp
   *
   * @param date Date type time
   * @return millisecond timestamp
   */
  public static long date2Millis(final Date date) {
    return date.getTime();
  }

  /**
   * Change the timestamp to the Date type
   *
   * @param millis millisecond timestamp
   * @return Date Type Time
   */
  public static Date millis2Date(final long millis) {
    return new Date(millis);
  }

  /**
   * Obtain two time differences (unit: unit)
   * <P> Both time0 and time1 are yyyy-MM-dd HH: mm: ss </ p>
   *
   * @param time0 time string 0
   * @param time1 time string 1
   * @param unit unit type <Ul> <Li>
   * @return unit timestamp
   */
  public static long getTimeSpan(final String time0, final String time1,
      @TimeConstants.Unit final int unit) {
    return getTimeSpan(time0, time1, DEFAULT_FORMAT, unit);
  }

  /**
   * Obtain two time differences (unit: unit)
   * <P> Both time0 and time1 are format </ p>
   *
   * @param time0 time string 0
   * @param time1 time string 1
   * @param format time format
   * @param unit unit type <Ul> <Li>
   * @return unit timestamp
   */
  public static long getTimeSpan(final String time0, final String time1, final DateFormat format,
      @TimeConstants.Unit final int unit) {
    return millis2TimeSpan(Math.abs(string2Millis(time0, format) - string2Millis(time1, format)),
        unit);
  }

  /**
   * Obtain two time differences (unit: unit)
   *
   * @param date0 Date Type Time 0
   * @param date1 Date Type Time 1
   * @param unit unit type <Ul> <Li>
   * @return unit timestamp
   */
  public static long getTimeSpan(final Date date0, final Date date1,
      @TimeConstants.Unit final int unit) {
    return millis2TimeSpan(Math.abs(date2Millis(date0) - date2Millis(date1)), unit);
  }

  private static long millis2TimeSpan(final long millis, @TimeConstants.Unit final int unit) {
    return millis / unit;
  }

}
package com.omisoft.hsracer.utils;

import android.arch.persistence.db.SupportSQLiteDatabase;


/**
 * Holds utility methods for working with DB
 * Created by dido on 26.04.17.
 */

public class DbUtils {

  /**
   * Batch executes sql
   */
  public static void executeBatchSql(SupportSQLiteDatabase db, String sql) {
    db.execSQL(sql);
  }

  /**
   * Replaces IOUtils.toString(Stream) method from the apache.commons.io library.
   * It's taken from stack overflow.
   */
  public static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

}

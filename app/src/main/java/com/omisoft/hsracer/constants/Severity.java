package com.omisoft.hsracer.constants;

/**
 * Error Severity
 * Created by dido on 07.06.17.
 */
public enum Severity {
  DEBUG(0),
  INFO(1),
  WARNING(2),
  ERROR(3),
  FATAL(4);

  private final int level;

  Severity(int level) {
    this.level = level;
  }

  public int value() {
    return this.level;
  }
}

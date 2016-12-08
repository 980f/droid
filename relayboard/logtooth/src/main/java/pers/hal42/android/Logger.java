package pers.hal42.android;

import android.util.Log;

/**
 * Copyright (C) by andyh created on 1/9/13 at 10:57 AM
 * convenience wrapper on android Log usage, not having to type a tag at each place of use.
 * Also serves as a nice place to set breakpoints to trap otherwise discarded exception messages.
 */
public class Logger {
  public final String logTag;

  public Logger(String tag) {
    logTag = tag;
  }

  public void i(String format, Object... args) {
    Log.i(logTag, String.format(format, args));
  }

  public void d(String format, Object... args) {
    Log.d(logTag, String.format(format, args));
  }

  public void e(String format, Object... args) {
    Log.e(logTag, String.format(format, args));
  }

}

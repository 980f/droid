package pers.hal42.android

import android.util.Log

/**
 * Copyright (C) by andyh created on 1/9/13 at 10:57 AM
 * convenience wrapper on android Log usage, not having to type a tag at each place of use.
 * Also serves as a nice place to set breakpoints to trap otherwise discarded exception messages.
 */
class Logger(val logTag: String?) {

  fun i(format: String, vararg args: Any) {
    Log.i(logTag?:"?", String.format(format, *args))
  }

  fun d(format: String, vararg args: Any) {
    Log.d(logTag?:"?", String.format(format, *args))
  }

  fun e(format: String, vararg args: Any) {
    Log.e(logTag?:"?", String.format(format, *args))
  }

}

val Dbg=Logger("Application")
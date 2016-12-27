package pers.hal42.android

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.widget.Button

/**
 * Copyright (C) by andyh created on 10/17/12 at 6:18 PM
 */

class ActivityLauncher (legend: CharSequence,val ctx: Context, val activity: Class<*>): Button(ctx) {
  init {
    setText(legend)
    setOnClickListener { launch() }
  }

  fun launch() {
    val parms=Intent(ctx, activity)
//    parms.putExtra()

    context.startActivity(parms)
  }

}

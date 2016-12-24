package pers.hal42.android

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.Button

/**
 * Copyright (C) by andyh created on 10/17/12 at 6:18 PM
 */
class ActivityLauncher : Button {
  protected var activity?: Class<*>//todo: determine if there is a suitable base class instead of the '?' (which however does match Android Intent arguments)

  fun launcherFor(cls: Class<*>) {
    activity = cls
    setOnClickListener { launch() }
  }

  fun launch() {
    val context = context
    context.startActivity(Intent(context, activity))
  }

  /////////////////////////
  constructor(context: Context) : super(context) {
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
  }
}

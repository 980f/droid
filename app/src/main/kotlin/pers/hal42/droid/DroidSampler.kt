package pers.hal42.droid

import android.os.Bundle
import pers.hal42.android.EasyActivity
import pers.hal42.android.NumberEditor
import pers.hal42.android.ViewFormatter

/**
 * This is cut down quite a bit from the app I discovered the bug in.
 * I was trying to edit an integer, but found that I was doing a bunch of int<->float conversions which must be explicit in Kotlin.
 * I ran the EditText with just ,
 *
 * */

class DroidSampler : EasyActivity(2) {
  internal val myView: ViewFormatter by lazy { makeText(-1) }
  internal var red = 3
    set(value) {
      myView.printf("\nred set to: %d", value)
    }
  internal var floating = false

  fun toggleFloating(doit: Boolean): Boolean {
    if (doit) {
      floating = !floating
    }
    return floating
  }

  fun toggleBuggy(doit: Boolean): Boolean {
    if (doit) {
      gridManager.fixBug = !gridManager.fixBug
    }
    return gridManager.fixBug
  }

  /** */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    makeButton("Show Value") { red = red }
    makeButton("Clear Display") { myView.cls() }
    gridManager.addToggle("Floating", "Integer") { v -> toggleFloating(v) }
    gridManager.addToggle("Bug Fixed","Buggy") { v -> toggleBuggy(v) }
    makeLauncher(NumberEditor.Connection("Edit Value", "set red time (seconds)", floating, false, { red.toFloat() }, { value -> red = value.toInt() }), -1)

    //this reference to myView creates it so must occur in an appropriate place to set its screen position.
    myView.printf("Click Popup the Editor to test\nToggle the bug to disable the workaround")
    myView.printf("\nrev 16")


  }

}

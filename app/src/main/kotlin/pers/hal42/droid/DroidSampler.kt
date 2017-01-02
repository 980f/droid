package pers.hal42.droid

import android.os.Bundle
import pers.hal42.android.*

/**
 * This is cut down quite a bit from the app I discovered the bug in.
 * I was trying to edit an integer, but found that I was doing a bunch of int<->float conversions which must be explicit in Kotlin.
 *
 * */

class DroidSampler : EasyActivity(2) {
  internal val myView: ViewFormatter by lazy { makeText(-1) }
  internal var red = 3
    set(value) {
      myView.printf("\nred set to: %d", value)
      field = value
    }

  internal val editorConnection = NumberEditor.Connection("Edit Value", "set red time (seconds)", false, false, { red.toFloat() }, { value -> red = value.toInt() })

  fun toggleFloating(doit: Boolean): Boolean {
    if (doit) {
      myView.printf("\neditorConnection %s",editorConnection.toString())
      editorConnection.asInteger = !editorConnection.asInteger
    }
    return editorConnection.asInteger
  }

  fun toggleBuggy(doit: Boolean): Boolean {
    if (doit) {
      GridManager.fixBug = !GridManager.fixBug
    }
    return GridManager.fixBug
  }

  /** */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    makeButton("Show Value") { red = red }
    makeButton("Clear Display") { myView.cls() }
    gridManager.addToggle("Floating", "Integer") { v -> toggleFloating(v) }
    gridManager.addToggle("Bug Fixed", "Buggy") { v -> toggleBuggy(v) }

    makeLauncher(editorConnection, -1)

    //this reference to myView creates it so must occur in an appropriate place to set its screen position.
    myView.printf("Click Popup the Editor to test\nToggle the bug to disable the workaround")
    myView.printf("\nrev 18")
  }
  internal val dbg= Logger(localClassName)
}

package pers.hal42.droid

import android.os.Bundle
import pers.hal42.android.*

/**
 * This is cut down quite a bit from the app I discovered the bug in.
 * I was trying to edit an integer, but found that I was doing a bunch of int<->float conversions which must be explicit in Kotlin.
 * I didn't know much about the EditText options, "proper" use of them avoids the bug, but it is a bug nonetheless.
 *
 * Unfixed, when you give a string image of a number that has a decimal point in it to the EditText it initially displays the string as received,
 * but on first contact with it removes the dp visually, concatenating (in the display) the fractional digits with the integer ones.
 * After editing which doesn't erase all of the string the value returned on DONE has extra digits in it, extra meaning not shown on the screen.
 * */

class DroidSampler : EasyActivity(2) {
  internal val myView: ViewFormatter by lazy { makeText(-1) }
  internal var red = 3
    set(value) {
      myView.printf("\nred set to: %d", value)
      field = value
    }

  internal val editorConnection = NumberEditor.Connection("Edit Value", "set red time (seconds)", false, false, { red.toFloat() }, { value -> red = value.toInt() })

  /** */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    makeButton("Show Value") { red = red }  //show present value via triggering red.set().
    makeButton("Clear Display") { myView.cls() }

    makeLauncher(editorConnection, -1) //makes a button which popsup an EditText.

    //this reference to myView creates it so must occur in an appropriate place to set its screen position.
    myView.printf("Click Popup the Editor to test\nSee GridManager fixBug member to enable workaround")
    myView.printf("\nrev 19") //making sure I actually downloaded an update.
  }
}

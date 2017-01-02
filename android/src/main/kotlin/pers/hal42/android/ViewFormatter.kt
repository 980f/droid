package pers.hal42.android

import android.os.Handler
import android.widget.TextView
import java.text.MessageFormat

/**
 * Copyright (C) by andyh created on 1/10/13 at 3:41 PM
 */
class ViewFormatter(val view: TextView) {
  //can't talk to gui on any old thread (sigh, it ain't that hard a thing to implement but no, the whole world has to individually work around the unwillingness to build a message queue into gui elements.)
  private val handler: Handler = Handler() //Handler constructor apparently knows its thread.

  /** schedules @param ess for appending to display, @returns ess */
  fun post(ess: String): String {
    handler.post { view.append(ess) }
    return ess
  }

  /** schedules setting the background color of this view. */
  fun setBackgroundColor(color: Int) {
    handler.post { view.setBackgroundColor(color) }
  }

  /** uses MessageFormat.format to produce text that is appended to the view.
   * @returns the formatted string  */
  fun format(mformat: String, vararg args: Any): String {
    if (args.isEmpty()) {//expedite frequent case
      return post(mformat)
    }
    return post(MessageFormat.format(mformat, *args))
  }

  /** uses String.format to produce text that is appended to the view.
   * @returns the formatted string */
  fun printf(cformat: String, vararg args: Any): String {
    if (args.isEmpty()) {//expedite frequent case
      return post(cformat)
    }
    return post(String.format(cformat, *args))
  }

  /** converts @param achar ascii code into a string that can be displayed. */
  fun putc(achar: Int) {//byte stream to character
    if (achar >= 0) {
// TextViews don't directly support appending a single character, so we have to waste a bunch of time creating a string intermediate. */
      post("$achar.toChar()")
    } else {//presumably it is something like a return code from a failed stream read
      //#nada
    }
  }

  /** output an end of line */
  fun endl() {
    putc(10) //linux newlines, this is for Android ;-)
  }

  /** clear screen:
   *   makeButton("Clear Display") { myView.cls() } */
  fun cls() {
    handler.post { view.text = "" }
  }
}

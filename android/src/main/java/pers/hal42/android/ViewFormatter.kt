package pers.hal42.android

import android.widget.TextView

import java.text.MessageFormat

/**
 * Copyright (C) by andyh created on 1/10/13 at 3:41 PM
 */
class ViewFormatter(var view: TextView?) {

  /** uses MessageFormat.format to produce text that is appended to the view. Tolerates a null view. */
  fun format(mformat: String, vararg args: Any): String {
    if (args.isEmpty()) {//expedite frequent case

        view?.append(mformat)

      return mformat
    }
    val ess = MessageFormat.format(mformat, *args)
    view?.append(ess)
    return ess
  }

  /** uses String.format to produce text that is appended to the view. Tolerates a null view. */
  fun printf(cformat: String, vararg args: Any): String {
    val ess = String.format(cformat, *args)
    if (view != null) {
      view!!.append(ess)
    }
    return ess
  }

  /** TextViews don't directly support appending a single character, so we have to waste a bunch of time creating a string intermediate. */
  fun putc(achar: Int) {
    if (view != null) {
      if (achar >= 0) {
        val arf = StringBuilder(1)
        arf.append(achar.toChar())
        view!!.append(arf)
      } else {
        //is return code from a failed stream read
      }
    }
  }

  /** output an end of line */
  fun endl() {
    putc('\n')
  }

  /** clear screen  */
  fun cls() {
    if (view != null) {
      view!!.text = ""
    }
  }
}

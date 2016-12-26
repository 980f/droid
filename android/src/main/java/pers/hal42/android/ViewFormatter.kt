package pers.hal42.android

import android.graphics.Color
import android.os.Handler
import android.widget.TextView

import java.text.MessageFormat

/**
 * Copyright (C) by andyh created on 1/10/13 at 3:41 PM
 */
class ViewFormatter(var view: TextView?) {
  //can't talk to gui on any olld thread (bastards, it ain't that hard a thing to implement but no, teh whole world has to work around the unwillingness to build a message queue into gui elements.
  private val handler:Handler=Handler();

  fun post(ess:String){
    handler.post { view?.append(ess) }
  }


  fun setBackgroundColor(color: Int){
    handler.post { view?.setBackgroundColor(color) }
  }

  /** uses MessageFormat.format to produce text that is appended to the view. Tolerates a null view. */
  fun format(mformat: String, vararg args: Any): String {
    if (args.isEmpty()) {//expedite frequent case
      view?.append(mformat)
      return mformat
    }
    val ess = MessageFormat.format(mformat, *args)
    post(ess)
    return ess
  }

  /** uses String.format to produce text that is appended to the view. Tolerates a null view. */
  fun printf(cformat: String, vararg args: Any): String {
    val ess = String.format(cformat, *args)
    post(ess)
    return ess
  }

  /** TextViews don't directly support appending a single character, so we have to waste a bunch of time creating a string intermediate. */
  fun putc(achar: Int) {//byte stream to character
      if (achar >= 0) {
        val arf = achar.toChar() //);//StringBuilder(1)
//        arf.append(achar.toChar())
        post("$arf");//arf.toString())
      } else {
        //is return code from a failed stream read
      }
  }

  /** output an end of line */
  fun endl() {
    putc(10) //linux newlines, this is for Android ;-)
  }

  /** clear screen  */
  fun cls() {
    handler.post {view?.setText("")}
  }
}

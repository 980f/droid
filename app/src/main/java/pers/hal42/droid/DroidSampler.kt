package pers.hal42.droid

import android.graphics.Color
import android.os.Bundle
import android.view.View
import pers.hal42.android.EasyActivity
import pers.hal42.android.ViewFormatter
import java.util.*


class DroidSampler : EasyActivity() {
  internal var myView: ViewFormatter? = null
  internal var countdown: Timer? = null
  internal var sets: List<TimerSet>? = null
  internal var currentSet: TimerSet? = null
  internal var tremain: Int = 0


  private fun updateTimeview() {
    --tremain
    if (currentSet == null) {//#leave non ternary for debug.
      setColor(Color.BLUE)
    } else {
      setColor(currentSet!!.colorForTimeRemaining(tremain))
    }

  }

  private fun setColor(color: Int) {
    myView!!.view.setBackgroundColor(color)
  }

  //run every second
  fun startTimer() {
    countdown!!.scheduleAtFixedRate(object : TimerTask() {
      override fun run() {
        updateTimeview()
      }
    }, 0, 1000)
  }

  private fun myClick(view: View) {//! the given view is related to the button.
    myView!!.format("\nYou are oppressing me! ")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    gridManager.columnCount = 2
    makeButton("Press me") { v -> myClick(v) }
    makeColorButton("Greenish", Color.GREEN)
    makeColorButton("Redish", Color.RED)
    makeColorButton("Blueish", Color.BLUE)

    myView = makeText(2)

  }

  private fun makeColorButton(colorname: String, colorcode: Int) {
    makeButton(colorname) { myView!!.view.setBackgroundColor(colorcode) }
  }

  /**
   * a triplet of parameters, how long each of the three phases lasts.
   * e.g. 5 minutes green, 2 minutes yellow, 30 seconds red. Total time 7.5 minutes.
   */
  class TimerSet {

    private val green: Int = 0
    private val yellow: Int = 0
    private val red: Int = 0


    fun totalTime(): Int {
      return green + yellow + red
    }

    fun colorForTimeRemaining(tremain: Int): Int {
      if (tremain < 0) {
        return Color.BLACK
      } else if (tremain < red) {
        return Color.RED
      } else if (tremain < yellow + red) {
        return Color.YELLOW
      } else if (tremain < green + yellow + red) {
        return Color.GREEN
      } else {
        return Color.BLUE
      }
    }

  }
}

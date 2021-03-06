package pers.hal42.droid

import android.graphics.Color
import android.os.Bundle
import pers.hal42.android.EasyActivity
import pers.hal42.android.NumberEditor
import pers.hal42.android.PolitePeriodicTimer
import pers.hal42.android.ViewFormatter
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty


class DroidSampler : EasyActivity(3) {
  internal val myView: ViewFormatter by lazy { makeText(-1) }
  internal val timing: PolitePeriodicTimer = PolitePeriodicTimer(1000)//once a second and startup stopped as we aren't init'd


  //  internal var sets: List<TimerSet>? = null
  internal val currentSet: TimerSet = TimerSet()
  internal var tremain: Int = 0
  internal var running: Boolean = false

  private fun doubleProp(prop:KMutableProperty<Int> ){
    prop.setter.call (this,2+prop.getter.call(this))
  }

  private fun updateTimeview() {
    if (running) {
      myView.cls()
      --tremain
      if (tremain > 0) {
        myView.format("{0}", tremain)
      } else if (tremain < 0) {
        if (tremain < -currentSet.totalTime()) {
          myView.format("Over >{0}!", currentSet.totalTime())
          running = false
        } else {
          myView.format("Over {0}!", -tremain)
        }
      } else {
        myView.format("Time's UP!")
      }
      setColor(currentSet.colorForTimeRemaining(tremain))
    }
  }

  private fun setColor(color: Int) {
    myView.setBackgroundColor(color)
  }

  private fun testTimer() {
    tremain = currentSet.totalTime()
    running = true
    timing.resume()
  }

  /** */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    makeColorButton("Greenish", Color.GREEN)
    makeColorButton("Yellowish", Color.YELLOW)
//    makeColorButton("Redish", Color.RED)
    makeLauncher(NumberEditor.Connection("Reddish", "set red time", true, false, { currentSet.red.toFloat() }, { value -> currentSet.red = value.toInt() }))
    makeButton(-1, "Start Timer") { testTimer() }

    myView.printf("Toast Timer")  //this reference to myView creates it so must occur in an appropriate place to set its screen position.

    //we will eventually make this more dynamic
//    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    myView.view.keepScreenOn = true

    timing.tasklist.add { updateTimeview() }

    myView.printf("\n local arg ${tremain}")

    var prop=DroidSampler::tremain

    doubleProp(prop)
    myView.printf("\n local arg ${tremain}")

  }

  /** a button that when pressed sets the background of the text area to the given @param colorcode */
  private fun makeColorButton(colorname: String, colorcode: Int) {
    makeButton(colorname) { myView.setBackgroundColor(colorcode) }
  }

  /**
   * a triplet of parameters, how long each of the three phases lasts.
   * e.g. 5 minutes green, 2 minutes yellow, 30 seconds red. Total time 7.5 minutes.
   */
  class TimerSet(var green: Int = 5, var yellow: Int = 2, var red: Int = 1) {

    fun totalTime(): Int {
      return green + yellow + red
    }

    fun colorForTimeRemaining(tremain: Int): Int {
      if (tremain < 0) {
        return Color.LTGRAY
      } else if (tremain < red) {
        return Color.RED
      } else if (tremain < yellow + red) {
        return Color.YELLOW
      } else if (tremain < green + yellow + red) {
        return Color.GREEN
      } else {
        return Color.MAGENTA
      }
    }

  }
}

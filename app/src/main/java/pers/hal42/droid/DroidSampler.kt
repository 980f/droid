package pers.hal42.droid

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import pers.hal42.android.EasyActivity
import pers.hal42.android.ViewFormatter
import java.util.*
import kotlin.concurrent.timerTask


class DroidSampler : EasyActivity(3) {
  internal var myView: ViewFormatter? = null
  internal var countdown: Timer = java.util.Timer(false)
  internal val countTask: TimerTask= object : TimerTask() {
    override fun run() {
      updateTimeview()
    }
  }
//  internal var sets: List<TimerSet>? = null
  internal val currentSet: TimerSet = TimerSet()
  internal var tremain: Int = 0

  private fun updateTimeview() {
    if(tremain!=0) {
      myView?.cls()
      --tremain
      if(tremain> 0) {
        myView?.format("Time remaining: {0}", tremain)
      } else if(tremain<0){
        if (tremain < -currentSet.totalTime()) {
          myView?.format("Over by More than: {0}", currentSet.totalTime())
        } else {
          myView?.format("Over Time by: {0}", -tremain)
        }
      } else {
        myView?.format("Time's UP!")
      }
      setColor(currentSet.colorForTimeRemaining(tremain))
    }
  }

  private fun setColor(color: Int) {
    myView?.setBackgroundColor(color)
  }

  //run every second
  fun startTimer() {
  }

  private fun testTimer() {
    tremain = 30
    currentSet.distribute(tremain)
    startTimer()
  }

  private fun textGeneratorClick() {//! the given view is related to the button.
    myView?.format("\nYou are oppressing me! ")
  }

/***/
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//works    makeButton("Press me"){textGeneratorClick()}
    makeColorButton("Greenish", Color.GREEN)
    makeColorButton("Redish", Color.RED)
    makeColorButton("Blueish", Color.BLUE)
    makeButton("Start Timer") { testTimer() }

    myView = makeText(-1)
    myView?.printf("Toast Timer")

    myView?.view?.keepScreenOn  //this did not do anything discernable
    //we will eventually make this more dynamic
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    countdown.scheduleAtFixedRate(countTask  , 0, 1000)

  }

  /** a button that when pressed sets the background of the text area to the given @param colorcode */
  private fun makeColorButton(colorname: String, colorcode: Int) {
    makeButton(colorname) { myView?.setBackgroundColor(colorcode) }
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

    fun distribute(total: Int, long: Float = 0.6F, medium: Float = 0.3F, short: Float = 0.1F) {
      red = (total * short).toInt()
      green = (total * long).toInt()
      yellow = (total * medium).toInt()
    }

  }
}

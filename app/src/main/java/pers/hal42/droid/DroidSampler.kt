package pers.hal42.droid

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import pers.hal42.android.EasyActivity
import pers.hal42.android.ViewFormatter
import java.util.*
import kotlin.concurrent.timer


class DroidSampler : EasyActivity() {
  internal var myView: ViewFormatter? = null
  internal var countdown: Timer = java.util.Timer(false)
  internal var sets: List<TimerSet>? = null
  internal val currentSet: TimerSet = TimerSet()
  internal var tremain: Int = 0
  internal val viewUpdater=Handler()
/*
    Timer myTimer = new Timer();
      myTimer.schedule(new TimerTask() {
         @Override
         public void run() {UpdateGUI();}
      }, 0, 1000);

   }

   private void UpdateGUI() {
      i++;
      //tv.setText(String.valueOf(i));
      myHandler.post(myRunnable);
   }


* */

  private fun updateTimeview() {
    --tremain
    setColor(currentSet.colorForTimeRemaining(tremain))
  }

  private fun setColor(color: Int) {
    myView!!.view.setBackgroundColor(color)
  }

  //run every second
  fun startTimer() {
    countdown?.scheduleAtFixedRate(object : TimerTask() {
      override fun run() {
        updateTimeview()
      }
    }, 0, 1000)
  }

  private fun testTimer(view:View){
    tremain=50
    startTimer()
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

    makeButton("Start Timer") {v->startTimer()}

  }

  private fun makeColorButton(colorname: String, colorcode: Int) {
    makeButton(colorname) { myView!!.view.setBackgroundColor(colorcode) }
  }

  /**
   * a triplet of parameters, how long each of the three phases lasts.
   * e.g. 5 minutes green, 2 minutes yellow, 30 seconds red. Total time 7.5 minutes.
   */
  class TimerSet {

    private val green: Int = 5
    private val yellow: Int = 2
    private val red: Int = 1


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

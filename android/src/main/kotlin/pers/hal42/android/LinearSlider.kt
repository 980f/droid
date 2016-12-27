package pers.hal42.android

import android.content.Context
import android.widget.SeekBar

/**
 * Copyright (C) by andyh created on 11/9/12 at 3:18 PM
 */
class LinearSlider(context: Context) : SeekBar(context) {

  init {
    max = quanta.toInt()
  }

  fun setValue(dee: Double) {
    if (dee < 0) {
      progress = 0
    } else if (dee > 1.0) {
      progress = max
    } else {
      progress = (quanta * dee).toInt()
    }
  }

  val value: Float
    get() {
      val dee = progress.toFloat()
      return dee / quanta
    }

  fun getValue(scalar: Float): Float {
    val dee = progress.toFloat()
    return dee * scalar / quanta
  }

  fun getValue(min: Float, max: Float): Float {
    val dee = progress.toFloat()
    return min + dee * (max - min) / quanta
  }

  companion object {

    val quanta = (1 shl 22).toFloat()//
  }

}

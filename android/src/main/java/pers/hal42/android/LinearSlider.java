package pers.hal42.android;

import android.content.Context;
import android.widget.SeekBar;

/**
* Copyright (C) by andyh created on 11/9/12 at 3:18 PM
*/
public class LinearSlider extends SeekBar {

  public LinearSlider(Context context) {
    super(context);
    setMax((int) quanta);
  }

  public void setValue(double dee) {
    if (dee < 0) {
      setProgress(0);
    } else if (dee > 1.0) {
      setProgress(getMax());
    } else {
      setProgress((int) (quanta * dee));
    }
  }

  public float getValue() {
    float dee = getProgress();
    return dee / quanta;
  }

  public float getValue(float scalar) {
    float dee = getProgress();
    return dee*scalar / quanta;
  }

  public float getValue(float min,float max) {
    float dee = getProgress();
    return min+(dee*(max-min) / quanta);
  }

  public static final float quanta = 1 << 22;//

}

package pers.hal42.droid;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pers.hal42.android.EasyActivity;
import pers.hal42.android.ViewFormatter;


public class DroidSampler extends EasyActivity {
  ViewFormatter myView;
  Timer countdown;
  List<TimerSet> sets;
  TimerSet currentSet;
  int tremain;


  private void updateTimeview() {
    if (currentSet == null) {//#leave non ternary for debug.
      setColor(Color.BLUE);
    } else {
      setColor(currentSet.colorForTimeRemaining(tremain));
    }

  }

  private void setColor(final int color) {
    myView.view.setBackgroundColor(color);
  }

  //run every second
  public void startTimer() {
    countdown.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        updateTimeview();
      }
    }, 0, 1000);
  }

  private void myClick(View view) {//! the given view is related to the button.
    myView.format("\nYou are oppressing me! ");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    gridManager.setColumnCount(4);
    makeButton("Press me", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        myClick(v);
      }
    });
    makeColorButton("Greenish",Color.GREEN);
    makeColorButton("Redish",Color.RED);
    makeColorButton("Blueish",Color.BLUE);

    myView = makeText();

  }

  private void makeColorButton(String colorname,final int colorcode) {
    makeButton(colorname, new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        myView.view.setBackgroundColor(colorcode);
      }
    });
  }

  /**
   * a triplet of parameters, how long each of the three phases lasts.
   * e.g. 5 minutes green, 2 minutes yellow, 30 seconds red. Total time 7.5 minutes.
   */
  public static class TimerSet {

    private int green;
    private int yellow;
    private int red;


    public int totalTime() {
      return green + yellow + red;
    }

    public int colorForTimeRemaining(int tremain) {
      if (tremain < 0) {
        return Color.BLACK;
      } else if (tremain < red) {
        return Color.RED;
      } else if (tremain < yellow + red) {
        return Color.YELLOW;
      } else if (tremain < green + yellow + red) {
        return Color.GREEN;
      } else {
        return Color.BLUE;
      }
    }

  }
}

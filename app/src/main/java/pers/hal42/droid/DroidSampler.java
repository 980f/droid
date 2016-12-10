package pers.hal42.droid;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

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
    makeButton("Press me", this::myClick);
    myView = makeText();

//    sets = new LinkedList<>();

  }

  /**
   * a triplet of parameters, how long each of the three phases lasts.
   * e.g. 5 minutes green, 2 minutes yellow, 30 seconds red. Total time 7.5 minutes.
   */
  public static class TimerSet implements Parcelable {
    public static final Parcelable.Creator<TimerSet> CREATOR = new Parcelable.Creator<TimerSet>() {
      public TimerSet createFromParcel(Parcel in) {
        return new TimerSet(in);
      }

      public TimerSet[] newArray(int size) {
        return new TimerSet[size];
      }
    };
    private int green;
    private int yellow;
    private int red;

    private TimerSet(Parcel in) {
      green = in.readInt();
      yellow = in.readInt();
      red = in.readInt();
    }

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

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
      parcel.writeInt(green);
      parcel.writeInt(yellow);
      parcel.writeInt(red);
    }


  }
}

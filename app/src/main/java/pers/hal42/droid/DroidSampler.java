package pers.hal42.droid;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pers.hal42.android.GriddedActivity;
import pers.hal42.android.ViewFormatter;


public class DroidSampler extends GriddedActivity {
  ViewFormatter myView;
  Timer countdown;
  List<TimerSet> sets;
  TimerSet currentSet;
  int tremain;
  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  private GoogleApiClient client;

  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  public Action getIndexApiAction() {
    Thing object = new Thing.Builder()
      .setName("DroidSampler Page") // TODO: Define a title for the content shown.
      // TODO: Make sure this auto-generated URL is correct.
      .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
      .build();
    return new Action.Builder(Action.TYPE_VIEW)
      .setObject(object)
      .setActionStatus(Action.STATUS_TYPE_COMPLETED)
      .build();
  }

  @Override
  public void onStart() {
    super.onStart();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.connect();
    AppIndex.AppIndexApi.start(client, getIndexApiAction());
  }

  @Override
  public void onStop() {
    super.onStop();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    AppIndex.AppIndexApi.end(client, getIndexApiAction());
    client.disconnect();
  }

  private void updateTimeview() {
    if(currentSet == null) {//#leave non ternary for debug.
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

  private void myClick(View view) {
    myView.format("\nYou are oppressing me!");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    myView = new ViewFormatter(add(TextView.class));
    final Button button = add(Button.class);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        myClick(view);
      }
    });
    button.setText("Press me");

    sets = new LinkedList<>();


    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
      if(tremain < 0) {
        return Color.BLACK;
      } else if(tremain < red) {
        return Color.RED;
      } else if(tremain < yellow + red) {
        return Color.YELLOW;
      } else if(tremain < green + yellow + red) {
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

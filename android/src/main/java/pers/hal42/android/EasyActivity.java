package pers.hal42.android;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by andyh on 12/9/16.
 * add common components to grid.
 */

public class EasyActivity extends GriddedActivity {
  public Button makeButton(String legend, View.OnClickListener clicker) {
    final Button button = add(Button.class);
    button.setText(legend);
    if (clicker != null) {
      button.setOnClickListener(clicker);
    }
    return button;
  }

  public ViewFormatter makeText() {
    ViewFormatter view = new ViewFormatter(add(TextView.class));
    return view;
  }
  //todo: add facility for a click on a text view generating a new activity with a single view which has the same text as the clocked on textview.
}

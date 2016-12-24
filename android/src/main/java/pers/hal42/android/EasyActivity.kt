package pers.hal42.android

import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * Created by andyh on 12/9/16.
 * add common components to grid.
 */

open class EasyActivity : GriddedActivity() {
  fun makeButton(legend: String, clicker: View.OnClickListener?): Button {
    val button = add(Button::class.java)
    button.text = legend
    if (clicker != null) {
      button.setOnClickListener(clicker)
    }
    return button
  }

  fun makeText(width: Int): ViewFormatter {
    val view = ViewFormatter(add(TextView::class.java, width > 0))
    return view
  }
  //todo: add facility for a click on a text view generating a new activity with a single view which has the same text as the clocked on textview.
}

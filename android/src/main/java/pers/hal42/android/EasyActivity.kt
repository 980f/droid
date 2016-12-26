package pers.hal42.android

import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * Created by andyh on 12/9/16.
 * add common components to grid.
 */

open class EasyActivity(columns:Int ) : GriddedActivity(columns) {

  fun makeButton(span:Int, legend: String, clicker: ()->Unit): Button {
    val button = add(Button::class.java, span, true)
    button.text = legend

    button.setOnClickListener(object : View.OnClickListener {
      //java under the hood needs an interface so we wrap our functional in one:
      override fun onClick(view: View) {
        clicker()
      }
    })

    return button
  }

  fun makeButton(legend: String, clicker: () -> Unit): Button {
    return makeButton(1,legend,clicker)
  }

  fun makeText(width: Int): ViewFormatter {
    val view = ViewFormatter(add(TextView::class.java, width ))
    return view
  }

  //todo: add facility for a click on a text view generating a new activity with a single view which has the same text as the clicked on textview.
}

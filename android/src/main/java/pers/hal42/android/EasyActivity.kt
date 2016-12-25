package pers.hal42.android

import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * Created by andyh on 12/9/16.
 * add common components to grid.
 */

open class EasyActivity : GriddedActivity() {

//  fun makeButton(legend: String, clicker: (View)->Unit): Button {
//    val button = add(Button::class.java)
//    button.text = legend
//
//    button.setOnClickListener(object : View.OnClickListener {
//      override fun onClick(view:View){
//        clicker(view)
//      }
//    })
//
//    return button
//  }

  fun makeButton(legend: String, clicker: ()->Unit): Button {
    val button = add(Button::class.java)
    button.text = legend

    button.setOnClickListener(object : View.OnClickListener {
      override fun onClick(view:View){
        clicker()
      }
    })

    return button
  }


  fun makeText(width: Int): ViewFormatter {
    val view = ViewFormatter(add(TextView::class.java, width > 0))
    return view
  }
  //todo: add facility for a click on a text view generating a new activity with a single view which has the same text as the clocked on textview.
}

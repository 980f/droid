package pers.hal42.android

import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.*

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
////////////////
  val popups: MutableMap<Int,NumberEditor.Connection> = HashMap()

  fun launch(connection: NumberEditor.Connection){
      val intent= Intent( applicationContext,  NumberEditor::class.java)
      connection.sendParams(intent)
      popups[connection.uniqueID()]=connection
      startActivityForResult(intent,connection.uniqueID())
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    val connection=popups[requestCode]
    connection?.accept(data)
    popups.remove(requestCode)
  }

  /** create and add button that when clicked launches an editor */
  fun makeLauncher(connection: NumberEditor.Connection,span: Int=1): Button {
    val button = makeButton(span,connection.legend){
      launch(connection)
    }
    return button
  }


  //todo: add facility for a click on a text view generating a new activity with a single view which has the same text as the clicked on textview.
}

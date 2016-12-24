package pers.hal42.android

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView

import java.lang.reflect.Constructor

/**
 * Copyright andyh  10/17/12 3:55 PM
 * todo: "clear screen" and reset cursor.
 */
class GridManager(context: Context) : GridLayout(context) {

  /**
   * grid position generator.
   * While public there is typically only one instance per GridManager and that instance is part of the GridManager object.
   * As a consequence of making row and col members public their values are checked and constrained before each use herein.
   */
  inner class Cursor(var grid: GridLayout//for maxcol or maxrow logic
  ) {
    var col: Int = 0
    var row: Int = 0

    init {
      col = 0
      row = 0
    }

    /**
     * @param span number of desired cells.
     * *
     * @return layout for given span, except if <0 or would exceed end of line then remaining on line
     */
    @JvmOverloads fun next(span: Int = 1): GridLayout.LayoutParams {
      var span = span
      val columnCount = grid.columnCount
      if (col >= columnCount) { //wrap line
        col = 0
        ++row
      }
      if (span < 0 || //hack for when it is inconvenient to call eol() due to default args.
        span > columnCount - col) { //do not wrap middle of entity nor auto expand grid.
        span = columnCount - col//and if that is negative let it blow for now
      }
      val layout = GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col, span))
      col += span
      return layout
    }

    /**
     * @return layout of remaining cells in this row
     */
    fun eol(): GridLayout.LayoutParams {
      return next(-1)
    }

  }

  var cursor: Cursor

  init {
    cursor = Cursor(this)
  }

  @Throws(IllegalArgumentException::class)
  fun <K : View> add(viewClass: Class<K>, span: Int, fillWidth: Boolean): K {
    try {
      val ctor = viewClass.getConstructor(Context::class.java)
      val viewObject = ctor.newInstance(context)

      val layout = cursor.next(span)
      if (fillWidth) {
        layout.setGravity(Gravity.FILL_HORIZONTAL)
      }
      super.addView(viewObject, layout)//
      return viewObject
    } catch (e: Exception) {
      e.printStackTrace()
      throw IllegalArgumentException(this.javaClass.name, e)
    }

  }

  @Throws(IllegalArgumentException::class)
  fun <K : View> add(viewClass: Class<K>): K {
    return add(viewClass, 1, false)
  }

  fun <K : View> add(viewClass: Class<K>, span: Int): K {
    return add(viewClass, span, false)
  }

  @Throws(IllegalArgumentException::class)
  fun <K : View> add(viewClass: Class<K>, fillWidth: Boolean): K {
    return add(viewClass, 1, fillWidth)
  }

  @JvmOverloads fun addDisplay(fixedText: CharSequence, span: Int = -1): TextView {
    val child = add(TextView::class.java, span, true) //true: text boxes should fill their space so that they don't change shape as much
    child.setBackgroundColor(Color.WHITE) //alh: should figure out how to do style sheet like stuff. til then hardcode personal prefs.
    child.setTextColor(Color.BLACK)
    child.text = fixedText
    return child
  }

  fun addTextEntry(prompt: CharSequence): EditText {
    val editor = add(EditText::class.java)
    editor.setText(prompt)
    return editor
  }

  @Deprecated("") //not yet finished
  @JvmOverloads fun addNumberEntry(initialValue: Float, span: Int = -1): EditText {
    val editor = add(EditText::class.java, span)
    //todo: set input constraint so that number keyboard is invoked.
    editor.setText(initialValue.toString())
    return editor
  }

  @JvmOverloads fun addButton(legend: CharSequence, span: Int = 1): Button {
    val button = add(Button::class.java, span, true)//true: typically want buttons to be as big as possible for fat fingers.
    button.text = legend
    return button
  }

  fun addButton(legend: CharSequence, action: View.OnClickListener): Button {
    return addButton(legend, 1, action)
  }

  fun addButton(legend: CharSequence, span: Int, action: View.OnClickListener?): Button {
    val button = addButton(legend, span)
    if (action != null) {
      button.setOnClickListener(action)
    }
    return button
  }

  fun addLauncher(legend: CharSequence, cls: Class<out Activity>): ActivityLauncher {
    val button = add(ActivityLauncher::class.java)
    button.activity = cls
    button.text = legend
    return button
  }

  @JvmOverloads fun addSlider(span: Int = -1): LinearSlider {
    return add(LinearSlider::class.java, span, true)
  }

}//not yet finished

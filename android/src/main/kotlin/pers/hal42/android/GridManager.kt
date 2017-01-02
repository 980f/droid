package pers.hal42.android

import android.content.Context
import android.text.InputType.*
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.GridLayout

/**
 * Copyright andyh  10/17/12 3:55 PM
 */
class GridManager(context: Context) : GridLayout(context) {
  val dbg: Logger = Logger(GridManager::class.simpleName)
  val fixBug = false

  /**
   * grid position generator.
   * While public there is typically only one instance per GridManager and that instance is part of the GridManager object.
   * As a consequence of making row and col members public their values are checked and constrained before each use herein.
   */
  class Cursor(val grid: GridLayout, var col: Int = 0, var row: Int = 0) {

    fun clear() {
      row = 0
      col = 0
    }

    /**
     * @param span number of desired cells.
     * *
     * @return layout for given span, except if <0 or would exceed end of line then remaining on line
     */
    fun next(span: Int = 1): GridLayout.LayoutParams {
      val columnCount = grid.columnCount
      if (col >= columnCount) { //wrap line
        col = 0
        ++row
      }
      val spanish = //constrained value of given span
        if (span < 0 || //hack for when it is inconvenient to call eol() due to default args.
          span > columnCount - col) { //do not wrap middle of entity nor auto expand grid.
          columnCount - col//and if that is negative let it blow for now
        } else {
          span
        }
      val layout = GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col, spanish))
      col += spanish
      return layout
    }

  }

  var cursor: Cursor = Cursor(this)

  /**
   * for view items already created this adds it to this grid.
   * @returns the viewObject passed in.
   */
  fun <K : View> add(viewObject: K, span: Int, fillWidth: Boolean): K {
    val layout = cursor.next(span)
    if (fillWidth) {
      layout.setGravity(Gravity.FILL_HORIZONTAL)
    }
    super.addView(viewObject, layout) //
    return viewObject
  }

  /**
   * for view items with a context constructor you can use this method to create one and add it to this grid.
   * @returns the new instance
   */
  fun <K : View> add(viewClass: Class<K>, span: Int, fillWidth: Boolean): K {
    val ctor = viewClass.getConstructor(Context::class.java)
    val viewObject = ctor.newInstance(context)
    return add(viewObject, span, fillWidth)
  }

  fun <K : View> add(viewClass: Class<K>): K {
    return add(viewClass, 1, false)
  }

  fun <K : View> add(viewClass: Class<K>, span: Int): K {
    return add(viewClass, span, false)
  }

  fun <K : View> add(viewClass: Class<K>, fillWidth: Boolean): K {
    return add(viewClass, 1, fillWidth)
  }


  fun addNumberEntry(initialValue: Float, asInteger: Boolean = true, hasSign: Boolean = false, span: Int = -1): EditText {
    val editor = add(EditText::class.java, span)
    var typecode = TYPE_CLASS_NUMBER   //x2
    var image = initialValue.toString()

    dbg.i("\n starting image <%s> as %x", image, typecode)

    if (fixBug) {
      if (asInteger) {//must clip trailing .0 or the EditText widget adds a trailing 0 to the returned number
        val clipat = image.indexOfFirst { it == '.' }
        if (clipat >= 0) {//todo: see what 0..-1 does in slice
          image = image.slice(0..clipat - 1)  //
          dbg.i(" clipped to <%s>", image)
        }
      } else {
        typecode += TYPE_NUMBER_FLAG_DECIMAL   //x2000
      }
      if (hasSign) {
        typecode += TYPE_NUMBER_FLAG_SIGNED   //x1000
      }
    }
    dbg.i(" typecode %x", typecode)  //x0002 when buggy, x2002 when fixed
    editor.inputType = typecode
    editor.setText(image)
    return editor
  }

}

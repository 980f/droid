package pers.hal42.android

import android.content.Context
import android.text.InputType.*
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout

/**
 * Copyright andyh  10/17/12 3:55 PM
 * todo: "clear screen" and reset cursor.
 */
class GridManager(context: Context) : GridLayout(context) {
  var fixBug = true

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
    //    @JvmOverloads
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

    /**
     * @return layout of remaining cells in this row
     */
    fun eol(): GridLayout.LayoutParams {
      return next(-1)
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
    var typecode = TYPE_CLASS_NUMBER
    var image = initialValue.toString()
    if (fixBug) {
      if (asInteger) {//must clip trailing .0 or the EditText widget adds a trailing 0 to the returned number
        val clipat = image.indexOfFirst { it == '.' }
        if (clipat >= 0) {//todo: see what 0..-1 does in slice
          image = image.slice(0..clipat - 1)  //
        }
      } else {
        typecode += TYPE_NUMBER_FLAG_DECIMAL
      }
    }
    if (hasSign) typecode += TYPE_NUMBER_FLAG_SIGNED
    editor.inputType = typecode
    editor.setText(image)
    return editor
  }

  fun addButton(legend: CharSequence, span: Int = 1): Button {
    val button = add(Button::class.java, span, true)//true: typically want buttons to be as big as possible for fat fingers.
    button.text = legend
    return button
  }

//  fun addButton(legend: CharSequence, action: View.OnClickListener): Button {
//    return addButton(legend, 1, action)
//  }

  fun addButton(legend: CharSequence, span: Int, action: View.OnClickListener?): Button {
    val button = addButton(legend, span)
    if (action != null) {
      button.setOnClickListener(action)
    }
    return button
  }


  /** trying to do this with just functionals was annoying*/
  class ToggleButton(context: Context, val whenOn:CharSequence, val whenOff:CharSequence, val action:(doit:Boolean)->Boolean) : Button(context) {
    init {
      setOnClickListener( { toggle()})
      updateLegend(action(false))
    }

    fun toggle() {
      val newstate=action(true) //invoke toggle, by using a return value we allow the toggler to veto the toggling.
      updateLegend(newstate)
    }

    private fun updateLegend(newstate: Boolean) {
      text = if (newstate) whenOn else whenOff
    }

  }

  /** @param action if sent a 1 to actually toggle, a 0 to read the present state, on toggle must return the new value of the state*/
  fun addToggle(whenOn:CharSequence,whenOff:CharSequence,span:Int=1,wide:Boolean=false,action:(doit:Boolean)->Boolean):ToggleButton {
    val button=ToggleButton(this.context,whenOn,whenOff,action)
    add(button,span,wide)
    return button
  }

}

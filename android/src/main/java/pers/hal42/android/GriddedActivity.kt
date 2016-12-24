package pers.hal42.android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.GridLayout

import java.lang.reflect.Constructor

/**
 * Copyright (C) by andyh created on 10/23/12 at 4:14 PM
 * An activity with a grid layout.
 * The number of rows and columns are read from the savedInstanceState bundle indexed by the derived class name, i.e you should extend from GriddActivity rather than just add widgets to a directly instantiated one.
 * To keep this class uncoupled we shall not add utilties to it, we will derive an class with convenience functions.
 */
open class GriddedActivity : Activity() {
  protected var gridManager: GridManager
  fun getIntState(savedInstanceState: Bundle, key: String, defaultValue: Int): Int {
    return savedInstanceState.getInt(javaClass.canonicalName + "." + key, defaultValue)
  }

  /**
   * Called when the activity is first created.
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    gridManager = GridManager(this)
    var columnCount = 1
    var rows = 0
    if (savedInstanceState != null) {
      columnCount = getIntState(savedInstanceState, "columns", 1)
      rows = getIntState(savedInstanceState, "rows", 0)
    }
    if (columnCount > 0) {//don't feed bad value to android
      gridManager.columnCount = columnCount
    }
    if (rows > 0) {//don't feed bad value to android
      gridManager.rowCount = rows
    }
    setContentView(gridManager)//todo: bundle for optional layout param
  }

  @Throws(IllegalArgumentException::class)
  fun <K : View> add(viewClass: Class<K>): K {
    try {
      val ctor = viewClass.getConstructor(Context::class.java)
      val viewObject = ctor.newInstance(this)
      gridManager.addView(viewObject)
      return viewObject
    } catch (e: Exception) {
      e.printStackTrace()
      throw IllegalArgumentException(this.javaClass.name, e)
    }

  }

  @Throws(IllegalArgumentException::class)
  fun <K : View> add(viewClass: Class<K>, fillWidth: Boolean): K {
    try {
      val layout = GridLayout.LayoutParams()
      if (fillWidth) {
        layout.setGravity(Gravity.FILL_HORIZONTAL)
      }

      val ctor = viewClass.getConstructor(Context::class.java)
      val viewObject = ctor.newInstance(this)


      gridManager.addView(viewObject, layout)//
      return viewObject
    } catch (e: Exception) {
      e.printStackTrace()
      throw IllegalArgumentException(this.javaClass.name, e)
    }

  }

  fun ShowObject(obj: Any) {
    AcknowledgeActivity.Acknowledge(obj.toString(), gridManager.context)
  }

  fun ShowStackTrace(wtf: Throwable) {
    AcknowledgeActivity.Acknowledge(wtf.message, this)
  }

}

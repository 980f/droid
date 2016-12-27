package pers.hal42.android

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * Copyright (C) by andyh created on 10/23/12 at 4:14 PM
 * An activity with a grid layout.
 * The number of rows and columns are read from the savedInstanceState bundle indexed by the derived class name, i.e you should extend from GriddActivity rather than just add widgets to a directly instantiated one.
 * To keep this class uncoupled we shall not add utilties to it, we will derive an class with convenience functions.
 */
open class GriddedActivity(var columns: Int, var rows: Int = 0) : Activity() {
  protected val gridManager: GridManager by lazy { GridManager(this) } //NPE if we try to instantiate this before onCreate is called: GridManager(this)

  fun getIntState(savedInstanceState: Bundle?, key: String, defaultValue: Int): Int {
    return savedInstanceState?.getInt(javaClass.canonicalName + "." + key, defaultValue) ?: defaultValue
  }

  /**
   * Called when the activity is first created.
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    columns = getIntState(savedInstanceState, "columns", columns)
    rows = getIntState(savedInstanceState, "rows", rows)

    if (columns > 0) {//don't feed bad value to android
      gridManager.columnCount = columns
    }
    if (rows > 0) {//don't feed bad value to android
      gridManager.rowCount = rows
    }
    setContentView(gridManager)//todo: bundle for optional layout param
  }

  fun <K : View> add(viewClass: Class<K>, span: Int = 1, fillWidth: Boolean = true): K {
    return gridManager.add(viewClass, span, fillWidth)
  }

  fun <K : View> add(viewClass: Class<K>): K {
    return gridManager.add(viewClass)
  }


  fun ShowObject(obj: Any) {
    AcknowledgeActivity.Acknowledge(obj.toString(), gridManager.context)
  }

  fun ShowStackTrace(wtf: Throwable) {
    AcknowledgeActivity.Acknowledge(wtf.message ?: "Ignored Error:", this)
  }

}

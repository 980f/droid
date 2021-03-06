package pers.hal42.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;

import java.lang.reflect.Constructor;

/**
 * Copyright (C) by andyh created on 10/23/12 at 4:14 PM
 */
public class GriddedActivity extends Activity {
  public GridManager gridManager; //todo: change to protected.

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    gridManager = new GridManager(this);
    int columnCount = 1;
    int rows = 0;
    if (savedInstanceState != null) {
      columnCount = savedInstanceState.getInt("pers.hal42.android.GriddedActivity.columns", 1); //todo: find scope of given bundle to see if we can drop the class specific prefixes in our keys.
      rows = savedInstanceState.getInt("pers.hal42.android.GriddedActivity.rows", 0);
    }
    if (columnCount > 0) {
      gridManager.setColumnCount(columnCount);
    }
    if (rows > 0) {
      gridManager.setRowCount(rows);
    }
    setContentView(gridManager);//todo: bundle for optional layout param
  }

  public <K extends View> K add(Class<K> viewClass) throws IllegalArgumentException {
    try {
      Constructor<K> ctor = viewClass.getConstructor(Context.class);
      K viewObject = ctor.newInstance(this);
      gridManager.addView(viewObject);
      return viewObject;
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException(this.getClass().getName(), e);
    }
  }

  public <K extends View> K add(Class<K> viewClass, boolean fillWidth) throws IllegalArgumentException {
    try {
      Constructor<K> ctor = viewClass.getConstructor(Context.class);
      K viewObject = ctor.newInstance(this);

      GridLayout.LayoutParams layout = new GridLayout.LayoutParams();
      if (fillWidth) {
        layout.setGravity(Gravity.FILL_HORIZONTAL);
      }
      gridManager.addView(viewObject, layout);//
      return viewObject;
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException(this.getClass().getName(), e);
    }
  }

  public void ShowObject(Object obj){
    AcknowledgeActivity.Acknowledge(obj.toString(),gridManager.getContext());
  }

  public void ShowStackTrace(Throwable wtf){
    AcknowledgeActivity.Acknowledge(wtf.getMessage(), this);
  }

}

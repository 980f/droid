package pers.hal42.android;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Copyright (C) by andyh created on 10/17/12 at 6:18 PM
 */
public class ActivityLauncher extends Button {
  protected Class<?> activity;//todo: determine if there is a suitable base class instead of the '?'

  public void setActivity(Class<?> cls){
    activity=cls;
    setOnClickListener(new View.OnClickListener(){
      public void onClick(View v) {
        launch();
      }
    } );
  }

  public void launch(){
    Context context=getContext();
    context.startActivity(new Intent(context, activity));
  }
  /////////////////////////
  public ActivityLauncher(Context context) {
    super(context);
  }

  public ActivityLauncher(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ActivityLauncher(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
}

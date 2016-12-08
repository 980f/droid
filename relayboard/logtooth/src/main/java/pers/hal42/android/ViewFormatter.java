package pers.hal42.android;

import android.widget.TextView;

import java.text.MessageFormat;

/**
 * Copyright (C) by andyh created on 1/10/13 at 3:41 PM
 */
public class ViewFormatter {
  public TextView view;

  public ViewFormatter(TextView view) {
    this.view = view;
  }

  /** uses MessageFormat.format to produce text that is appended to the view. Tolerates a null view.*/
  public String format(String mformat, Object... args) {
    if(args.length==0){//expedite frequent case
      if (view != null) {
        view.append(mformat);
      }
      return mformat;
    }
    String ess = MessageFormat.format(mformat, args);
    if (view != null) {
      view.append(ess);
    }
    return ess;
  }

  /** uses String.format to produce text that is appended to the view. Tolerates a null view.*/
  public String printf(String cformat, Object... args) {
    String ess = String.format(cformat, args);
    if (view != null) {
      view.append(ess);
    }
    return ess;
  }

  /** TextViews don't directly support appending a single character, so we have to waste a bunch of time creating a string intermediate.*/
  public void putc(int achar){
    if(view!=null) {
      if (achar >= 0) {
        StringBuilder arf = new StringBuilder(1);
        arf.append((char) achar);
        view.append(arf);
      } else {
        //is return code from a failed stream read
      }
    }
  }

  /** output an end of line*/
  public void endl(){
    putc('\n');
  }

  /** clear screen */
  public void cls() {
    if(view!=null){
      view.setText("");
    }
  }
}

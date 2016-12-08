package pers.hal42.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/** A screen that displays a string from the intent's bundle.
 * Copyright (C) by andyh created on 11/1/12 at 3:26 PM
 */
public class AcknowledgeActivity extends Activity {
  public static final String messageKey= AcknowledgeActivity.class.getCanonicalName();
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    CharSequence message = intent.getCharSequenceExtra(messageKey);

    TextView textView = new TextView(this);
    setContentView(textView);

    textView.setTextSize(40);//todo: configurable font attributes
    textView.setText(message);

  }

  /**
   * basic usage:
   * */
  public static void Acknowledge(CharSequence message, Context someActivity){
    Intent intent = new Intent(someActivity, AcknowledgeActivity.class);
    intent.putExtra(AcknowledgeActivity.messageKey, message);
    someActivity.startActivity(intent);
  }

}
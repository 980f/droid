package pers.hal42.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView

/** A screen that displays a string from the intent's bundle.
 * Copyright (C) by andyh created on 11/1/12 at 3:26 PM
 */
class AcknowledgeActivity : Activity() {
  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val intent = intent
    val message = intent.getCharSequenceExtra(messageKey)

    val textView = TextView(this)
    setContentView(textView)

    textView.textSize = 40f//todo: configurable font attributes
    textView.text = message

  }

  companion object {
    private val messageKey = AcknowledgeActivity::class.java.canonicalName//#cached

    /**
     * basic usage:
     */
    fun Acknowledge(message: CharSequence, someActivity: Context) {
      val intent = Intent(someActivity, AcknowledgeActivity::class.java)
      intent.putExtra(AcknowledgeActivity.messageKey, message)
      someActivity.startActivity(intent)
    }
  }

}
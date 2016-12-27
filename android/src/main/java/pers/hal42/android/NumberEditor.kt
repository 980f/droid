package pers.hal42.android

import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import kotlin.reflect.KMutableProperty



/** edit the given property */
class NumberEditor(val target: KMutableProperty<Int>, val desc:String) : EasyActivity(1) {
  val starting:Int by lazy { target.getter.call() }
  val editor: EditText by lazy {gridManager.addNumberEntry(starting.toFloat())}

  fun onEnter(){
    val newvalue= editor.text.toString().toInt()
    target.setter.call(newvalue)
  }

  fun onEditorAction(actionId: Int  ){
    print("Editor action id: "); print(actionId)
    onEnter()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //setContentView(R.layout.activity_number_editor)
    val starting =target.getter.call()
    makeText(-1).format(desc,starting)
//    editor=gridManager.addNumberEntry(starting.toFloat())
    editor.selectAll()
    editor.setOnEditorActionListener(object : TextView.OnEditorActionListener {
      override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        onEditorAction(p1)
        return true
      }
    })
  }
}

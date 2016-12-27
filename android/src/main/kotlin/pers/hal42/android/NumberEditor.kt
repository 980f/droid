package pers.hal42.android

import android.content.Intent
import android.os.Bundle
import android.widget.EditText

class EditorConnection(val legend: String, val desc: String, val getter: () -> Float, val setter: (Float) -> Unit) {

  companion object response {
    const val resultKey = "result"
    const val descKey = "desc"
    const val legendKey = "legend"
    const val startingKey = "starting"

    fun entry(value: Float): Intent {
      val intent = Intent("garbage")
      intent.putExtra(resultKey, value)
      return intent
    }
  }

  /** the container will map activity results to this EditorConnection via this code*/
  fun uniqueID() = this.hashCode()

  fun sendParams(intent: Intent) {
    intent.putExtra(descKey, desc)
    intent.putExtra(legendKey, legend)
    intent.putExtra(startingKey, getter())
  }

  /** get entry, if defective feed original value back. */
  fun accept(data: Intent?) {
    val result = data?.getFloatExtra(resultKey, getter()) ?: getter()
    setter(result)
  }

}

/** edit the given property */
class NumberEditor() : EasyActivity(1) {
  var editor: EditText? = null

  fun onEditorAction(actionId: Int) {
    print("Editor action id: "); print(actionId)
    val result =
      try {
        editor?.text.toString().toFloat()
      } catch (ex: NumberFormatException) {
        -66.1F
      }
    setResult(RESULT_OK, EditorConnection.response.entry(result))
    finish()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val starting = intent.getFloatExtra("starting", 0F)
    val desc = intent.getStringExtra("desc")//,"Unknown item")
    makeText(-1).format(desc, starting)
    editor = gridManager.addNumberEntry(starting)
    editor!!.selectAll()
    editor!!.setOnEditorActionListener { p0, p1, p2 ->
      onEditorAction(p1)
      true
    }
  }
}

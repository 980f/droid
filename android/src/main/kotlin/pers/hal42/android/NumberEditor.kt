package pers.hal42.android

import android.content.Intent
import android.os.Bundle
import android.widget.EditText

class EditorConnection(val legend: String, val desc: String, val getter: () -> Int, val setter: (Int) -> Unit) {

  companion object response {
    const val resultKey = "result"
    const val descKey = "desc"
    const val legendKey = "legend"
    const val startingKey = "starting"

    fun entry(value: Int): Intent {
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
    val result = data?.getIntExtra(resultKey, getter()) ?: getter()
    setter(result)
  }

}

/** edit the given property */
class NumberEditor() : EasyActivity(1) {
  val editor: EditText? = null

  fun onEditorAction(actionId: Int) {
    print("Editor action id: "); print(actionId)
    val result =
      try {
        editor?.text.toString().toInt()
      } catch (ex: NumberFormatException) {
        -42
      }
    setResult(RESULT_OK, EditorConnection.response.entry(result))
    finish()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val starting = intent.getFloatExtra("starting", 0.0F)
    val desc = intent.getStringExtra("desc")//,"Unknown item")
    makeText(-1).format(desc, starting)
    val editor = gridManager.addNumberEntry(starting)
    editor.selectAll()
    editor.setOnEditorActionListener { p0, p1, p2 ->
      onEditorAction(p1)
      true
    }
  }
}

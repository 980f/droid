package pers.hal42.android

import android.content.Intent
import android.os.Bundle
import android.widget.EditText


/** edit the given property */
class NumberEditor() : EasyActivity(1) {

  class Connection(val legend: String, val desc: String, val getter: () -> Float, val setter: (Float) -> Unit) {

    companion object response {
      const val resultKey = "result"
      const val descKey = "desc"
      const val legendKey = "legend"
      const val startingKey = "starting"

      fun entry(image: String?): Intent {
        val intent = Intent("NumberEntryResult")
        intent.putExtra(resultKey, image)
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

    /** accepting text so that we can add SI units to user entry */
    fun accept(data: Intent?) {
      data?.let {
        val image = data.getStringExtra(resultKey)
        image?.let {
          try {
            val result = image.toFloat()
            setter(result)
          } catch (ex: NumberFormatException) {
            //don't call anything.
          }
        }
      }
    }

  }




  var editor: EditText? = null

  fun onEditorAction(actionId: Int) {
    print("Editor action id: "); print(actionId)
    val result = editor?.text.toString()
    setResult(RESULT_OK, Connection.response.entry(result))
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
    editor.
  }
}

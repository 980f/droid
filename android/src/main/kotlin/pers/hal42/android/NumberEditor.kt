package pers.hal42.android

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.EditText
import pers.hal42.android.NumberEditor.Connection.X.asIntegerKey
import pers.hal42.android.NumberEditor.Connection.X.descKey
import pers.hal42.android.NumberEditor.Connection.X.startingKey

val dbg= Logger("NumberEditor")

/** edit the given property */
class NumberEditor() : EasyActivity(1) {

  class Connection(val legend: String, val desc: String,val asInteger:Boolean,val hasSign:Boolean, val getter: () -> Float, val setter: (Float) -> Unit) {

    companion object X {
      //todo: reflection
      const val resultKey = "result"
      const val descKey = "desc"
      const val legendKey = "legend"
      const val startingKey = "starting"
      const val asIntegerKey="asInteger"
      const val hasSignKey="hasSign"

      fun entry(image: String?): Intent {
        val intent = Intent("NumberEntryResult")
        intent.putExtra(resultKey, image)
        return intent
      }

      fun recreate(transport:Intent):Connection {
        val connection= Connection(
        transport.getStringExtra(legendKey),
        transport.getStringExtra(descKey),
        transport.getBooleanExtra(asIntegerKey,false),
          transport.getBooleanExtra(hasSignKey,true),
          {transport.getFloatExtra(startingKey,0F)},
          {Unit}
        )
        return connection
      }
    }

    /** the container will map activity results to this EditorConnection via this code*/
    fun uniqueID() = this.hashCode()

    fun sendParams(intent: Intent) {
      intent.putExtra(descKey, desc)
      intent.putExtra(legendKey, legend)
      intent.putExtra(asIntegerKey,asInteger)
      intent.putExtra(hasSignKey,hasSign)
      intent.putExtra(startingKey, getter())
    }

    /** accepting text so that we can add SI units to user entry */
    fun accept(data: Intent?) {
      data?.let {
        val image = data.getStringExtra(resultKey)
        image?.let {
          try {
            val result = image.toFloat()
            dbg.i("As string: %s, as number: %f",image,result)
            setter(result)
          } catch (ex: NumberFormatException) {
            //don't call anything.
            dbg.e("Bad Format: %s",image)
          }
        }
      }
    }

  }

  var editor: EditText? = null

  fun onEditorAction(actionId: Int) {
    if(actionId==IME_ACTION_DONE) {
      val text = editor?.text
      val result = text?.toString()
      setResult(RESULT_OK, Connection.X.entry(result))
      finish()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val connection=Connection.X.recreate(intent)
    val starting=connection.getter()
    makeText(-1).format(connection.desc, starting)

    editor = gridManager.addNumberEntry(starting,connection.asInteger,connection.hasSign)
    editor!!.selectAll()  //but touching the widget to bring up the keyboard only deletes one character (occasionally none)
    editor!!.setOnEditorActionListener { p0, p1, p2 ->
      onEditorAction(p1)
      true
    }
    editor!!.showSoftInputOnFocus=true  //despite upping sdk level this does not do what it suggests it does.
  }
}

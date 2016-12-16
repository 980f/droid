package pers.hal42.util

import kotlin.reflect.KClass

/**
 * Created by andyh on 12/9/16.
 * stores objects in properties files as marked by Storable annoation
 */

class StoredProperties {

  fun store(thing: Any) {
    for(val prop in thing.class) {println("p" $prop")}
//    val claz= thing::class
//    for(val annotation in claz.mem) {

     //   if (note.annotationType() == Storable::class.java) {
//          var key = (note as Storable).key()
//          if (key.length == 0) {
//            key = field.getName()
//          }
//          //todo: switch on type recursing fields
//          val claz = field.getDeclaringClass()
//          if (claz == Number::class.java) {
//            try {
//              val value = field.getDouble(thing)
//              super.setProperty(key, java.lang.Double.toString(value))
//            } catch (e: IllegalAccessException) {
//              e.printStackTrace()
//              //if we don't set property then it might have a stale value!!
//            }
//
//          } else if (claz == Array::class.java) {
//            //todo: iterate over array with array index as additional key
//          }
//          break//only support a single instance of our annotation.
//        }

//      }
//    }
  }

}

package pers.hal42.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by andyh on 12/9/16.
 * stores objects in properties files as marked by Storable annoation
 */

public class StoredProperties extends Properties {

  public void store(Object thing) {

    for(Field field : thing.getClass().getDeclaredFields()) {
      final Annotation[] annotations = field.getDeclaredAnnotations();//not using getDeclaredAnnotation(Storable.class) so that we can run on api level 15.
      for(Annotation note:annotations){
        if(note.annotationType()==Storable.class) {
          String key=((Storable)note).key();
          if (key.length()==0){
            key=field.getName();
          }
          //todo: switch on type recursing fields
          final Class<?> claz = field.getDeclaringClass();
          if(claz == Number.class) {
            try {
              double value=field.getDouble(thing);
              super.setProperty(key,Double.toString(value) );
            } catch(IllegalAccessException e) {
              e.printStackTrace();
              //if we don't set property then it might have a stale value!!
            }
          } else if(claz== Array.class){
            //todo: iterate over array with array index as additional key
          }
          break;//only support a single instance of our annotation.
        }

      }
    }
  }

}

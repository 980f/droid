package pers.hal42.util;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Storable {
  //marker for field to be asciified and transported
  String key() default "";//empty string results in Field's name being used as the key.
}

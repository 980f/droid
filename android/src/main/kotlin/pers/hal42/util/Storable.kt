package pers.hal42.util

//kotlin default: @Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Storable(//marker for field to be asciified and transported
  val key: String = ""//empty string results in Field's name being used as the key.
)

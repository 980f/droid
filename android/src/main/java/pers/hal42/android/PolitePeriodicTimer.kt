package pers.hal42.android

import java.util.*

/**
 * Created by andyh on 12/26/16.
 *
 * adapt functions to java interfaces
 * stifle pointless Timer state exceptions.
 */
class PolitePeriodicTimer( period: Int=1000, beRunning:Boolean=false) {

  //first pass implementation: add and remove tasks from this list to enable and disable them
  //todo:M add adaptors with an enable bit
  val tasklist:MutableList<()->Unit> = mutableListOf()  //#space needed between > and = , compiler error messages weren't helpful.
  fun doTasks(){
    for(task in tasklist){
      task()
    }
  }


  /** a non-running timer if pause'd then resume'd will start running */
  internal var pauseCounter:Int =0

  /** wrapped service*/
  internal var timer: Timer = java.util.Timer(false)
  /** adaptor, give one task to java timer, maintain our own list here using Kotlin list management */
  internal val tasker: TimerTask = object : TimerTask() {
    override fun run() {//mate Java rquirement for a function names run to our tasklist.
      if(pauseCounter==0) doTasks()
    }
  }

  init {

    try {
      timer.scheduleAtFixedRate(tasker  , 0, period.toLong())
      if(!beRunning){
        pause()
      }
    } catch(e: Exception) {
      pauseCounter=0x7FFFFFFF
    }
  }

  fun pause(){
    if(++pauseCounter < 0){  //guard against integer wrap
      --pauseCounter
    }
  }

  fun resume(){
    if(pauseCounter>0){
      --pauseCounter
    }
  }

}

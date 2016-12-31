/**
 * wrap common usages of System.currentTimeMillis
 */

package pers.hal42.util

class StopWatch @JvmOverloads constructor(hitTheFloorRunning: Boolean = true) {
  internal var started: Long = 0
  internal var stopped: Long = 0
  //is atomic so no synch needed.
  var isRunning: Boolean = false
    internal set

  fun startedAt(): Long {
    return started
  }

  fun seconds(): Double { //can be read while running
    return millis().toDouble() / Ticks.perSecond
  }

  fun millis(): Long { //can be read while running
    return (if (isRunning) System.currentTimeMillis() else stopped) - started
  }

  fun Start() {
    started = System.currentTimeMillis()
    stopped = started
    isRunning = true
  }

  fun Stop(): Long {
    if (isRunning) {
      stopped = System.currentTimeMillis()
      isRunning = false
    }
    return millis()
  }

  fun Reset() {
    isRunning = false
    started = 0
    stopped = started
  }

  init {
    Reset()
    if (hitTheFloorRunning) {
      Start()
    }
  }
}

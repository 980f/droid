package pers.hal42.util

import pers.hal42.util.ThreadX.sleepFor


/**
 * properly synchronized wait/notify mechanism
 * It most notably deals with the thing being waited for being completed before the wait is attempted.
 * usage:
 * call waiter.prepare() BEFORE triggering behavior that will eventually notify you.
 * call waiter.Start(...) to wait for notification. It returns "state" or you can get
 * that same value from the state() function.
 * All parameters are set by prepare(...) variations, internal defaults used for ones omitted
 * Start(...) variations only modify the parameters passed, retaining ones set by the last prepare()
 *
 *
 * see end of file for more extensive usage advice
 *
 *
 * NOTE: JVM requires that we synch on the same object that is used for wait and notify!
 */

class Waiter {

  enum class Status {
    Ready, Notified, Timedout, Interrupted, Excepted, Extending
  }

  private var millisecs: Long = 0
  private var allowInterrupt: Boolean = false

  /**
   * originally made a member instead of a local to reduce startup overhead in
   * Wait. primary use is to shorten successive timeouts when we sleep again
   * after ignoring an interrupt, conveniently also provides a response time
   */
  private val resleeper = StopWatch()
  private var state = Status.Ready
  /**
   * waitOnMe, safe object to do actual thread wait's on.
   */
  private val waitOnMe =  Object()

  /**
   * @returns internal state as human readable String
   */
  override fun toString(): String {
    return state.toString()
  }

  /**
   * something to do a switch upon... you might want to 'synch(myWaiter)' around
   * your switch for clarity, but such a synch is not required for rational use
   * of the waiter.

   * @returns psuedo enumeration of internal state
   */
  fun state(): Status {
    return state
  }
  /**
   * @returns milliseconds from wait until notify. only strictly valid if there
   * * WAS a notification. will be zero but not negative if notified before
   * * wait started.
   */
  fun elapsedTime(): Int {
    return resleeper.millis().toInt()
  }

  /**
   * @param millisecs becomes the wait time POSSIBLY IMMEDIATELY i.e. this can
   * *                  cause a timeout if changed while running. That is good.
   * *
   * @returns this
   */
  fun set(millisecs: Long): Waiter {
    this.millisecs = millisecs
    return this
  }

  /**
   * set

   * @param allowInterrupt sets whether to allow 'interrupted' state, else thread interrupts are ignored
   * *
   * @returns this
   */
  fun set(allowInterrupt: Boolean): Waiter {
    this.allowInterrupt = allowInterrupt
    return this
  }


  /**
   * setto, coordinated (synch'ed) setting of parameters

   * @param millisecs      long @see set(long)
   * *
   * @param allowInterrupt boolean @see set(boolean)
   * *
   * @returns this
   */
  internal fun setto(millisecs: Long, allowInterrupt: Boolean): Waiter {
    synchronized(waitOnMe) {
      //ensure atomic changing of the args. might be gratuitous but doesn't hurt.
      return set(millisecs).set(allowInterrupt)
    }
  }

  /**
   * @returns this
   * * the synch in prepare is now required. Clears any old notification or
   * * problem so that we can see a new one.
   */
  fun prepare(): Waiter {
    synchronized(waitOnMe) {
      state = Status.Ready
      resleeper.Reset()//lets us discover that we notified before waiting.
      return this
    }
  }

  /**
   * starts waiting

   * @returns state when waiting is done.
   */
  fun run(): Status {
    synchronized(waitOnMe) {
      try {
        resleeper.Start() //always a fresh start, no "lap time" on our stopwatches.
        if (state == Status.Extending) {//allow for extending before starting.
          state = Status.Ready
        }

        while (state == Status.Ready) {
          val waitfor = millisecs - resleeper.millis()
          if (waitfor < 0) {
            state = Status.Timedout  //timed out before waiting
          } else {
            try {
              waitOnMe.wait(waitfor) //will throw IllegalArgumentException if sleep time is negative...
            } catch (ie: InterruptedException) {
              Thread.interrupted()//clear thread's interrupted flag
              if (allowInterrupt) {
                state = Status.Interrupted
              } else {
                if (state == Status.Extending) {//any other state (besides Ready)will result in ending the wait when we do the continue.
                  state = Status.Ready
                }
                continue//interrupts are ignored
              }
            }

            if (state == Status.Extending) {
              state = Status.Ready//but resleeper is left alone, the time set by Extend() begins when that call occurs.
              continue//if notified we will exit the while, don't need to check for that here.
            }
            if (state == Status.Ready) {
              state = Status.Timedout
            }
          } //end normal wait
        }//end while still in ready state
      } catch (ex: Exception) {
        state = Status.Excepted
      } finally {
        resleeper.Stop()
      }
      return state()
    }//end synch
  }

  /**
   * stretch a wait in progress, or set wait time for next wait.

   * @param milliseconds time to continue waiting starting from NOW, not
   * *                     original start().
   * *
   * @returns the state, if not 'extending' then wasn't in a state legal to
   * * extend
   * * Note: if you use this you should always give an argument to prepare().
   */
  fun Extend(milliseconds: Long): Status {
    synchronized(waitOnMe) {
      if (state == Status.Ready || state == Status.Extending) { //can only extend when already running or extending
        this.millisecs = milliseconds
        state = Status.Extending
        resleeper.Start() //forget the past
        try {
          waitOnMe.notify() //internal notify, to make things more brisk.
        } catch (ex: Exception) { //especially null pointer exceptions
          state = Status.Excepted
        }

      } else {
        //        dbg.WARNING("was not in extendible state:" + state);
      }
      return state()
    }
  }

  /**
   * start waiting, setting all parameters

   * @param millisecs      long @see set(long)
   * *
   * @param allowInterrupt boolean @see set(boolean)
   * *
   * @returns what caused wait to terminate, @see run()
   */
  @JvmOverloads fun Start(millisecs: Long, allowInterrupt: Boolean = this.allowInterrupt): Status {
    setto(millisecs, allowInterrupt)
    //it is ok if any of the notification functions is called by another thread between these lines of code.
    return run()
  }

  /**
   * configure waiter but don't wait.
   * call run() to wait using these values.

   * @param millisecs      long @see set(long)
   * *
   * @param allowInterrupt boolean @see set(boolean)
   * *
   * @returns this
   */
  @JvmOverloads fun prepare(millisecs: Long, allowInterrupt: Boolean = false): Waiter {
    setto(millisecs, allowInterrupt)
    return prepare()
  }


  /**
   * polite and properly synch'd version of Object.notify()
   * private so that only certain states can be indicated.

   * @param newstate what a pending run() will return
   * *
   * @returns true if notify did NOT happen, mostly of academic interest.
   */

  private fun notifyThis(newstate: Status): Boolean {
    synchronized(waitOnMe) {
      state = newstate
      try {
        waitOnMe.notify()
        return false
      } catch (ex: Exception) {//especially null pointer exceptions
        state = Status.Excepted
        return true
      } finally {
        resleeper.Stop() //owner gets a repsonse time from this.
      }
    }
  }

  /**
   * normal wait complete notification, i.e. call this at point where an event has occured when the wait is being used to timeout waiting for that event.

   * @returns true if notify did NOT happen, mostly of academic interest.
   */
  fun Stop(): Boolean {
    return notifyThis(Status.Notified) //yes, overrides Interrupted and all other states.
  }

  /**
   * force a timeout NOW

   * @returns true if notify did NOT happen, mostly of academic interest.
   */
  fun forceTimeout(): Boolean {
    return notifyThis(Status.Timedout) //yes, overrides Interrupted and all other states.
  }

  /**
   * force an exception indication

   * @returns true if notify did NOT happen, mostly of academic interest.
   */
  fun forceException(): Boolean {
    return notifyThis(Status.Excepted) //terminates wait and indicates things are screwed
  }

  /**
   * create a Waiter, with legal but useless configuration.

   * @see Create
   */
  init {
    prepare()
  }

  /**
   * this exists to allow for the full wait time to pass before code proceeds,
   * useful for preventing "spam on error" loops I.e. when the wait is terminated by an error rather
   * than the preferred condition we might choose to wait for the same amount of time that we would have
   * if the wait had been terminate due to a timeout, so that other errors have the same realtime
   * behavior as 'no response'.
   */
  fun finishWaiting() {
    val remaining = millisecs - resleeper.millis()
    if (remaining > 0) {
      sleepFor(remaining)
    }
  }

  companion object {

    /**
     * @param millisecs      long @see set(long)
     * *
     * @param allowInterrupt boolean @see set(boolean)
     * *
     * @returns new configured Waiter
     */
    fun Create(millisecs: Long, allowInterrupt: Boolean): Waiter {
      return Waiter().setto(millisecs, allowInterrupt)
    }
  }

}
/**
 * Start, retain present 'allowInterrupt' setting

 * @param millisecs long @see set(long)
 * *
 * @returns what caused wait to terminate, @see run()
 */

/*
you must prepare() to wait
then execute code that will result later in a notify
  (which can do so instantly and on the same or another thread without a problem)
then run().

The functions Start(---) should be renamed run(---).

in each of the following usage guides
  '...' stands for the code that triggers the notify that we are waiting upon
  args stands for the wait time, allow interrupt, debugger values used by run()


usage 1: know wait arguments before trigger event:
prepare(args) ... run()

usage 2: know some wait arguments only after trigger event, such as when some
return value from the trigger code is needed to compute a wait time.
prepare() ... Start(args)

usage 3: args never change
use static Create() function

prepare() ... run()

*/

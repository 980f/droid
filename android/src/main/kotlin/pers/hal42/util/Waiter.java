package pers.hal42.util;

import static pers.hal42.util.ThreadX.sleepFor;


/**
 * properly synchronized wait/notify mechanism
 * It most notably deals with the thing being waited for being completed before the wait is attempted.
 * usage:
 * call waiter.prepare() BEFORE triggering behavior that will eventually notify you.
 * call waiter.Start(...) to wait for notification. It returns "state" or you can get
 * that same value from the state() function.
 * All parameters are set by prepare(...) variations, internal defaults used for ones omitted
 * Start(...) variations only modify the parameters passed, retaining ones set by the last prepare()
 * <p>
 * see end of file for more extensive usage advice
 * <p>
 * NOTE: JVM requires that we synch on the same object that is used for wait and notify!
 */

public class Waiter {

  public enum Status {Ready, Notified, Timedout, Interrupted, Excepted, Extending}

  /**
   * @param millisecs      long @see set(long)
   * @param allowInterrupt boolean @see set(boolean)
   * @returns new configured Waiter
   */
  public static Waiter Create(long millisecs, boolean allowInterrupt) {
    return new Waiter().setto(millisecs, allowInterrupt);
  }

  private long millisecs;
  private boolean allowInterrupt;

  /**
   * originally made a member instead of a local to reduce startup overhead in
   * Wait. primary use is to shorten successive timeouts when we sleep again
   * after ignoring an interrupt, conveniently also provides a response time
   */
  private StopWatch resleeper = new StopWatch();
  private Status state = Status.Ready;
  /**
   * waitOnMe, safe object to do actual thread wait's on.
   */
  private final Object waitOnMe = new Object();

  /**
   * @returns internal state as human readable String
   */
  public String toString() {
    return state.toString();
  }


  /**
   * something to do a switch upon... you might want to 'synch(myWaiter)' around
   * your switch for clarity, but such a synch is not required for rational use
   * of the waiter.
   *
   * @returns psuedo enumeration of internal state
   */
  public Status state() {
    return state;
  }

  /**
   * @returns whether present state matches @param possiblestate
   */
  public boolean is(Status possiblestate) {
    return state == possiblestate;
  }

  /**
   * @returns milliseconds from wait until notify. only strictly valid if there
   * WAS a notification. will be zero but not negative if notified before
   * wait started.
   */
  public int elapsedTime() {
    return (int) resleeper.millis();
  }

  /**
   * @param millisecs becomes the wait time POSSIBLY IMMEDIATELY i.e. this can
   *                  cause a timeout if changed while running. That is good.
   * @returns this
   */
  public Waiter set(long millisecs) {
    this.millisecs = millisecs;
    return this;
  }

  /**
   * set
   *
   * @param allowInterrupt sets whether to allow 'interrupted' state, else thread interrupts are ignored
   * @returns this
   */
  public Waiter set(boolean allowInterrupt) {
    this.allowInterrupt = allowInterrupt;
    return this;
  }


  /**
   * setto, coordinated (synch'ed) setting of parameters
   *
   * @param millisecs      long @see set(long)
   * @param allowInterrupt boolean @see set(boolean)
   * @returns this
   */
  Waiter setto(long millisecs, boolean allowInterrupt) {
    synchronized(waitOnMe) {//ensure atomic changing of the args. might be gratuitous but doesn't hurt.
      return set(millisecs).set(allowInterrupt);
    }
  }

  /**
   * @returns this
   * the synch in prepare is now required. Clears any old notification or
   * problem so that we can see a new one.
   */
  public Waiter prepare() {
    synchronized(waitOnMe) {
      state = Status.Ready;
      resleeper.Reset();//lets us discover that we notified before waiting.
      return this;
    }
  }

  /**
   * starts waiting
   *
   * @returns state when waiting is done.
   */
  public Status run() {
    synchronized(waitOnMe) {
      try {
        resleeper.Start(); //always a fresh start, no "lap time" on our stopwatches.
        if(state == Status.Extending) {//allow for extending before starting.
          state = Status.Ready;
        }

        while(state == Status.Ready) {
          long waitfor = millisecs - resleeper.millis();
          if(waitfor < 0) {
            state = Status.Timedout;  //timed out before waiting
          } else {
            try {
              waitOnMe.wait(waitfor); //will throw IllegalArgumentException if sleep time is negative...
            } catch(InterruptedException ie) {
              Thread.interrupted();//clear thread's interrupted flag
              if(allowInterrupt) {
                state = Status.Interrupted;
              } else {
                if(state == Status.Extending) {//any other state (besides Ready)will result in ending the wait when we do the continue.
                  state = Status.Ready;
                }
                continue;//interrupts are ignored
              }
            }
            if(state == Status.Extending) {
              state = Status.Ready;//but resleeper is left alone, the time set by Extend() begins when that call occurs.
              continue;//if notified we will exit the while, don't need to check for that here.
            }
            if(state == Status.Ready) {
              state = Status.Timedout;
            }
          } //end normal wait
        }//end while still in ready state
      } catch(Exception ex) {
        state = Status.Excepted;
      } finally {
        resleeper.Stop();//for a valiant attempt at figuring out when a problem occured.
      }
      return state();
    }//end synch
  }

  /**
   * stretch a wait in progress, or set wait time for next wait.
   *
   * @param milliseconds time to continue waiting starting from NOW, not
   *                     original start().
   * @returns the state, if not 'extending' then wasn't in a state legal to
   * extend
   * Note: if you use this you should always give an argument to prepare().
   */
  public Status Extend(long milliseconds) {
    synchronized(waitOnMe) {
      if(state == Status.Ready || state == Status.Extending) { //can only extend when already running or extending
        this.millisecs = milliseconds;
        state = Status.Extending;
        resleeper.Start(); //forget the past
        try {
          waitOnMe.notify(); //internal notify, to make things more brisk.
        } catch(Exception ex) { //especially null pointer exceptions
          state = Status.Excepted;
        }
      } else {
//        dbg.WARNING("was not in extendible state:" + state);
      }
      return state();
    }
  }

  /**
   * start waiting, setting all parameters
   *
   * @param millisecs      long @see set(long)
   * @param allowInterrupt boolean @see set(boolean)
   * @returns what caused wait to terminate, @see run()
   */
  public Status Start(long millisecs, boolean allowInterrupt) {
    setto(millisecs, allowInterrupt);
    //it is ok if any of the notification functions is called by another thread between these lines of code.
    return run();
  }

  /**
   * Start, retain present 'allowInterrupt' setting
   *
   * @param millisecs long @see set(long)
   * @returns what caused wait to terminate, @see run()
   */
  public final Status Start(long millisecs) {
    return Start(millisecs, this.allowInterrupt);
  }

  /**
   * configure waiter but don't wait.
   * call run() to wait using these values.
   *
   * @param millisecs      long @see set(long)
   * @param allowInterrupt boolean @see set(boolean)
   * @returns this
   */
  public Waiter prepare(long millisecs, boolean allowInterrupt) {
    setto(millisecs, allowInterrupt);
    return prepare();
  }

  public final Waiter prepare(long millisecs) {
    return prepare(millisecs, false);
  }


  /**
   * polite and properly synch'd version of Object.notify()
   * private so that only certain states can be indicated.
   *
   * @param newstate what a pending run() will return
   * @returns true if notify did NOT happen, mostly of academic interest.
   */
  private boolean notifyThis(Status newstate) {
    synchronized(waitOnMe) {
      state = newstate;
      try {
        waitOnMe.notify();
        return false;
      } catch(Exception ex) {//especially null pointer exceptions
        state = Status.Excepted;
        return true;
      } finally {
        resleeper.Stop(); //owner gets a repsonse time from this.
      }
    }
  }

  /**
   * normal wait complete notification, i.e. call this at point where an event has occured when the wait is being used to timeout waiting for that event.
   *
   * @returns true if notify did NOT happen, mostly of academic interest.
   */
  public boolean Stop() {
    return notifyThis(Status.Notified); //yes, overrides Interrupted and all other states.
  }

  /**
   * force a timeout NOW
   *
   * @returns true if notify did NOT happen, mostly of academic interest.
   */
  public boolean forceTimeout() {
    return notifyThis(Status.Timedout); //yes, overrides Interrupted and all other states.
  }

  /**
   * force an exception indication
   *
   * @returns true if notify did NOT happen, mostly of academic interest.
   */
  public boolean forceException() {
    return notifyThis(Status.Excepted); //terminates wait and indicates things are screwed
  }

  /**
   * create a Waiter, with legal but useless configuration.
   *
   * @see Create(...)
   */
  public Waiter() {
    prepare();
  }

  /**
   * this exists to allow for the full wait time to pass before code proceeds,
   * useful for preventing "spam on error" loops I.e. when the wait is terminated by an error rather
   * than the preferred condition we might choose to wait for the same amount of time that we would have
   * if the wait had been terminate due to a timeout, so that other errors have the same realtime
   * behavior as 'no response'.
   */
  public void finishWaiting() {
    long remaining = millisecs - resleeper.millis();
    if(remaining > 0) {
      sleepFor(remaining);
    }
  }

}

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

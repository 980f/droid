package pers.hal42.util

/**
 * utility methods that are handy with threads, especially dealing with Interrupt.
 * Most of this seems to be moot in Kotlin, one can pass a consumer to a producer.
 */

object ThreadX {
  /**
   * polite and properly synch'd version of Object.wait().
   * this one swallows interrupts

   * @returns whether NOT notified.
   */
  @JvmOverloads fun waitOn(obj: java.lang.Object, millisecs: Long, allowInterrupt: Boolean = false): Boolean {
    synchronized(obj) {
      val resleeper = StopWatch()//to shorten successive timeouts when we sleep again after an interrupt
      while (true) {
        try {
          val waitfor = millisecs - resleeper.millis()
          if (waitfor < 0) {
            return true  //timed out
          }
          obj.wait(waitfor) //will throw IllegalArgumentException if sleep time is negative...
          return resleeper.Stop() > millisecs //preferred exit
        } catch (ie: InterruptedException) {
          if (allowInterrupt) {
            return true
          } else {
            continue
          }
        } catch (ex: Exception) {
          return true
        }

      }
      return false  //formal, never get here.
    }
  }

  /**
   * polite and properly synch'd version of Object.notify()

   * @returns whether notify did NOT happen
   */
  fun notify(obj: java.lang.Object): Boolean {
    synchronized(obj) {
      try {
        obj.notify()
        return false
      } catch (ex: Exception) {//especially null pointer exceptions
        return true
      }

    }
  }

  /**
   * returns whether it completed its sleep (was NOT interrupted)
   */
  fun sleepFor(millisecs: Long): Boolean {
    var interrupted = false
    try {
      Thread.sleep(if (millisecs > 0) millisecs else 0)//sleep(0) should behave as yield()
    } catch (e: InterruptedException) {
      interrupted = true
    } finally {
      return !(Thread.interrupted() || interrupted)
    }
  }


  fun sleepFor(seconds: Double) {
    sleepFor(Ticks.forSeconds(seconds))
  }

  /**
   * caller is responsible for trying to make the thread stop()
   */
  fun waitOnStopped(mortal: Thread, maxwait: Long): Boolean {
    mortal.interrupt()
    try {
      mortal.join(maxwait)
      return false
    } catch (ex: Exception) {
      return true
    }

  }

//
//  fun wait(me: Thread, maxwait: Long): Boolean {
//    try {
//// ### not accessible,     me.wait(maxwait)
//      return false
//    } catch (ex: InterruptedException) {
//      return true
//    }
//
//  }

  fun join(it: Thread, maxwait: Long): Boolean {
    try {
      it.join(maxwait)
      return false
    } catch (ex: InterruptedException) {
      return true
    }

  }

//  Kotlin doesn't like assignment-in-test
//  fun RootThread(): ThreadGroup {
//    var treeTop = Thread.currentThread().threadGroup
//    var parent: ThreadGroup
//    while ((parent = treeTop.parent) != null) {
//      treeTop = parent
//    }
//    return treeTop
//  }

  //  public static TextList ThreadDump(ThreadGroup tg){
  //    if(tg==null){
  //      tg=RootThread();
  //    }
  //    TextList ul=new TextList();
  //    int threadCount = tg.activeCount();
  //    Thread [] list = new Thread [threadCount * 2]; // plenty of room this way
  //    int count = tg.enumerate(list);
  //    for(int i = 0; i<count; i++) {
  //      Thread t = list[i];
  //      if(t.getThreadGroup() == tg) {
  //        String name = t.getName() + "," + t.getPriority() + ((t.isDaemon()) ? ",daemon" : "");
  //        if(t instanceof ThreadReporter) {
  //          ThreadReporter sess = (ThreadReporter)t;
  //          name += sess.status();
  //        }
  //        ul.add(name);
  //      }
  //    }
  //    // print the child thread groups
  //    int groupCount = tg.activeGroupCount();
  //    ThreadGroup [] glist = new ThreadGroup [groupCount * 2]; // plenty of room this way
  //    groupCount = tg.enumerate(glist);
  //    for(int i = 0; i<groupCount; i++) {
  //      ThreadGroup g = glist[i];
  //      if(g.getParent() == tg) {
  //        ul.appendMore(ThreadDump(g));
  //      }
  //    }
  //    return ul;
  //  }
  //

}

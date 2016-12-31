package pers.hal42.util;

/**
 * utility methods that are handy with threads, especially deally with Interrupt.
 */

public class ThreadX {
  /**
   * polite and properly synch'd version of Object.wait().
   * this one swallows interrupts
   *
   * @returns whether NOT notified.
   */
  public static boolean waitOn(Object obj, long millisecs, boolean allowInterrupt) {
    synchronized(obj) {
      StopWatch resleeper = new StopWatch();//to shorten successive timeouts when we sleep again after an interrupt
      while(true) {
        try {
          long waitfor = millisecs - resleeper.millis();
          if(waitfor < 0) {
            return true;  //timed out
          }
          obj.wait(waitfor); //will throw IllegalArgumentException if sleep time is negative...
          return resleeper.Stop() > millisecs; //preferred exit
        } catch(InterruptedException ie) {
          if(allowInterrupt) {
            return true;
          } else {
            continue;
          }
        } catch(Exception ex) {
          return true;
        }
      }
    }
  }

  public static boolean waitOn(Object obj, long millisecs) {
    return waitOn(obj, millisecs, false);
  }

  /**
   * polite and properly synch'd version of Object.notify()
   *
   * @returns whether notify did NOT happen
   */
  public static boolean notify(Object obj) {
    synchronized(obj) {
      try {
        obj.notify();
        return false;
      } catch(Exception ex) {//especially null pointer exceptions
        return true;
      }
    }
  }

  /**
   * returns whether it completed its sleep (was NOT interrupted)
   */
  public static boolean sleepFor(long millisecs) {
    boolean interrupted = false;
    try {
      Thread.sleep(millisecs > 0 ? millisecs : 0);//sleep(0) should behave as yield()
    } catch(InterruptedException e) {
      interrupted = true;
    } finally {
      return !(Thread.interrupted() || interrupted);
    }
  }


  public static void sleepFor(double seconds) {
    sleepFor(Ticks.forSeconds(seconds));
  }

  /**
   * caller is responsible for trying to make the thread stop()
   */
  public static boolean waitOnStopped(Thread mortal, long maxwait) {
    mortal.interrupt();
    try {
      mortal.join(maxwait);
      return false;
    } catch(Exception ex) {
      return true;
    }
  }

  public static boolean wait(Thread me, long maxwait) {
    try {
      me.wait(maxwait);
      return false;
    } catch(InterruptedException ex) {
      return true;
    }
  }

  public static boolean join(Thread it, long maxwait) {
    try {
      it.join(maxwait);
      return false;
    } catch(InterruptedException ex) {
      return true;
    }
  }

  public static ThreadGroup RootThread() {
    ThreadGroup treeTop = Thread.currentThread().getThreadGroup();
    ThreadGroup parent;
    while((parent = treeTop.getParent()) != null) {
      treeTop = parent;
    }
    return treeTop;
  }

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

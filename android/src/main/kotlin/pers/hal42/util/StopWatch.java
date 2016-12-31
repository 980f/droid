/**
 * wrap common usages of System.currentTimeMillis
 */

package pers.hal42.util;

public class StopWatch {
  long started;
  long stopped;
  boolean running;

  public boolean isRunning() {
    return running; //is atomic so no synch needed.
  }

  public long startedAt() {
    return started;
  }

  public double seconds() { //can be read while running
    return ((double) millis()) / Ticks.perSecond;
  }

  public long millis() { //can be read while running
    return (running ? System.currentTimeMillis() : stopped) - started;
  }

  public void Start() {
    stopped = started = System.currentTimeMillis();
    running = true;
  }

  public long Stop() {
    if(running) {
      stopped = System.currentTimeMillis();
      running = false;
    }
    return millis();
  }

  public void Reset() {
    running = false;
    stopped = started = 0;
  }

  public StopWatch(boolean hitTheFloorRunning) {
    Reset();
    if(hitTheFloorRunning) {
      Start();
    }
  }

  public StopWatch() {
    this(true);
  }
}

package pers.hal42.util;

/**
 * Java timing is mostly done in milliseconds.
 * This class wraps converting human units into milliseconds.
 * It may seem gratuitous, but removes embedded constants.
 */

public class Ticks {

  public static final long perSecond = 1000L;
  public static final long perMinute = 60 * perSecond;
  public static final long perHour = 60 * perMinute;
  public static final long perDay = 24 * perHour;

  public static final long forSeconds(int seconds) {
    return seconds * perSecond;
  }

  public static final long forSeconds(double seconds) {
    return (long) (seconds * ((double) perSecond));
  }

  public static final long forMinutes(int minutes) {
    return minutes * perMinute;
  }

  public static final long forHours(int hours) {
    return hours * perHour;
  }

  public static final long forDays(int days) {
    return days * perDay;
  }

}

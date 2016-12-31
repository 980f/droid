package pers.hal42.util

/**
 * Java timing is mostly done in milliseconds.
 * This class wraps converting human units into milliseconds.
 * It may seem gratuitous, but removes embedded constants.
 */

object Ticks {

  const val perSecond = 1000L
  const val perSecondd = 1000F
  const val perMinute = 60 * perSecond
  const val perHour = 60 * perMinute
  const val perDay = 24 * perHour

  fun forSeconds(seconds: Int): Long {
    return seconds * perSecond
  }

  fun forSeconds(seconds: Double): Long {
    return (seconds * perSecondd).toLong()
  }

  fun forMinutes(minutes: Int): Long {
    return minutes * perMinute
  }

  fun forHours(hours: Int): Long {
    return hours * perHour
  }

  fun forDays(days: Int): Long {
    return days * perDay
  }

}

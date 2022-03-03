/*
 * Copyright 2022 David Bouyssié
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mdbx4s.util

//import java.util.{Calendar, Date, GregorianCalendar, TimeZone}
//import java.util.concurrent.Callable

object HoursDisplayOption extends Enumeration {
  type HrsOption = Value
  val NEVER, IFNOTZERO, ALWAYS = Value
}

object TimeUtils {

  private val DEFAULT_MESSAGE = "Operation completed in:" //$NON-NLS-1$

  def elapsedSinceNano(sinceNanos: Long, showHours: HoursDisplayOption.Value = HoursDisplayOption.NEVER): String = {
    nanoTimeAsString(_calcElapsedTimeNanos(sinceNanos), showHours)
  }

  private def _calcElapsedTimeNanos(sinceNanos: Long): Long = {
    val currentNanos = System.nanoTime()
    val elapsed = currentNanos - sinceNanos
    elapsed
  }

  def nanoTimeAsString(nanos: Long, showHours: HoursDisplayOption.Value): String = {
    val ms = nanos / 1000000
    val remainder = nanos % 1000000
    if (ms > 0) timeAsString(ms, showHours) + ':' + remainder / 1000 + "us"
    else nanos / 1000 + "us"
  }

  def timeAsString(millis: Long, showHours: HoursDisplayOption.Value): String = {

    val hours = millis / (1000 * 3600)
    val remainderHoursAsMs = millis % (1000 * 3600)

    var minutes = remainderHoursAsMs / (1000 * 60)
    val remainderMinutesAsMs = remainderHoursAsMs % (1000 * 60)

    val seconds = remainderMinutesAsMs / 1000
    val ms = remainderMinutesAsMs % 1000

    import HoursDisplayOption._

    if (showHours == ALWAYS || (showHours == IFNOTZERO && hours > 0))
      return s"${hours}h:${minutes}m:${seconds}s:${ms}ms"

    minutes += (hours * 60)

    s"${minutes}m:${seconds}s:${ms}ms"

    /*
    val calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT")) //$NON-NLS-1$
    calendar.setTime(elapsedDate)
    val days = calendar.get(Calendar.DAY_OF_YEAR)
    val hrs = ((days - 1) * 24) + calendar.get(Calendar.HOUR_OF_DAY)
    if ((showHrs eq HrsOption.ALWAYS) || ((showHrs eq HrsOption.IFNOTZERO) && hrs > 0)) return hrs + "h:" + calendar.get(Calendar.MINUTE) + "m:" + calendar.get(Calendar.SECOND) + "s:" + //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
      calendar.get(Calendar.MILLISECOND) + "ms"
    val min = (hrs * 60) + calendar.get(Calendar.MINUTE)
    min + "m:" + calendar.get(Calendar.SECOND) + "s:" + calendar.get(Calendar.MILLISECOND) + "ms" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$*/
  }

  /*def elapsedSince(sinceDate: Date): String = elapsedSince(sinceDate, HrsOption.NEVER)

  def elapsedSince(sinceDate: Date, showHrs: HrsOption.Value): String = {
    val dt = new Date(System.currentTimeMillis)
    val elapsed = dt.getTime - sinceDate.getTime
    timeAsString(elapsed, showHrs)
  }

  def elapsedSince(sinceMillis: Long): String = elapsedSince(new Date(sinceMillis))

  def elapsedSince(sinceMillis: Long, showHrs: HrsOption.Value): String = elapsedSince(new Date(sinceMillis), showHrs)

  def elapsedSinceNano(sinceNanos: Long): String = elapsedSinceNano(sinceNanos, HrsOption.NEVER)

  def elapsedSinceNanos(sinceNanos: Long): Long = {
    val currentNanos = System.nanoTime
    val elapsed = currentNanos - sinceNanos
    elapsed
  }

  def elapsedSinceNano(sinceNanos: Long, showHrs: HrsOption.Value): String = nanoTimeAsString(elapsedSinceNanos(sinceNanos), showHrs)

  def timeAsString(millis: Long, showHrs: HrsOption.Value): String = {
    val elapsedDate = new Date(millis)
    val calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT")) //$NON-NLS-1$
    calendar.setTime(elapsedDate)
    val days = calendar.get(Calendar.DAY_OF_YEAR)
    val hrs = ((days - 1) * 24) + calendar.get(Calendar.HOUR_OF_DAY)
    if ((showHrs eq HrsOption.ALWAYS) || ((showHrs eq HrsOption.IFNOTZERO) && hrs > 0)) return hrs + "h:" + calendar.get(Calendar.MINUTE) + "m:" + calendar.get(Calendar.SECOND) + "s:" + //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
      calendar.get(Calendar.MILLISECOND) + "ms"
    val min = (hrs * 60) + calendar.get(Calendar.MINUTE)
    min + "m:" + calendar.get(Calendar.SECOND) + "s:" + calendar.get(Calendar.MILLISECOND) + "ms" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  def nanoTimeAsString(nanos: Long, showHrs: HrsOption.Value): String = {
    val ms = nanos / 1000000
    val remainder = nanos % 1000000
    if (ms > 0) timeAsString(ms, showHrs) + ':' + remainder + "ns"
    else nanos + "ns"
  }

  def runWithTiming[T](callable: Callable[T]): T = runWithTiming(DEFAULT_MESSAGE, null, callable)

  def runWithTiming[T](showHrs: HrsOption.Value, callable: Callable[T]): T = runWithTiming(DEFAULT_MESSAGE, showHrs, callable)

  def runWithTiming[T](message: String, callable: Callable[T]): T = runWithTiming(message, null, callable)

  def runWithTiming[T](message: String, showHrs: HrsOption.Value, callable: Callable[T]): T = {
    val startTime = System.currentTimeMillis
    try callable.call
    catch {
      case e: Exception =>
        throw new RuntimeException(e)
    } finally if (showHrs != null) System.out.println(message + elapsedSince(startTime, showHrs))
    else System.out.println(message + elapsedSince(startTime))
  }

  def runWithNanoTiming[T](callable: Callable[T]): T = runWithNanoTiming(DEFAULT_MESSAGE, null, callable)

  def runWithNanoTiming[T](showHrs: HrsOption.Value, callable: Callable[T]): T = runWithNanoTiming(DEFAULT_MESSAGE, showHrs, callable)

  def runWithNanoTiming[T](message: String, callable: Callable[T]): T = runWithNanoTiming(message, null, callable)

  def runWithNanoTiming[T](message: String, showHrs: HrsOption.Value, callable: Callable[T]): T = {
    val startNano = System.nanoTime
    try callable.call
    catch {
      case e: Exception =>
        throw new RuntimeException(e)
    } finally if (showHrs != null) System.out.println(message + elapsedSinceNano(startNano, showHrs))
    else System.out.println(message + elapsedSinceNano(startNano))
  }*/
}

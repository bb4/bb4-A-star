// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import java.util.concurrent.TimeUnit

/** Elapsed-time helper using a monotonic clock (nanos), suitable for performance assertions in tests. */
class Watch private (private val startNanos: Long) {

  def this() = this(System.nanoTime())

  /** @return the elapsed time (in seconds) since this object was created. */
  def getElapsedSeconds: Double =
    TimeUnit.NANOSECONDS.toMillis(System.nanoTime - startNanos) / 1000.0
}

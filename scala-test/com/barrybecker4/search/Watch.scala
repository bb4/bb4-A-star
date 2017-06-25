// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.search

class Watch() {

  private val start: Long = System.currentTimeMillis

  /** @return the elapsed time (in seconds) since this object was created. */
  def getElapsedTime: Double = (System.currentTimeMillis - start) / 1000.0
}

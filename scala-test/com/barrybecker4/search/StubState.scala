// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

/**
  * @param id some unique identifier
  * @param estDistanceFromGoal the estimated distance to goal should be an optimistic estimate
  *   in order to be admissible by A*.
  * @author Barry Becker
  */
case class StubState(id: String, estDistanceFromGoal: Int) {

  /** @param id some unique identifier */
  def this(id: Int, estDistanceFromGoal: Int = 0) = {
    this(Integer.toString(id), estDistanceFromGoal)
  }

  private[search] def getDistanceFromGoal: Int = estDistanceFromGoal

  override def toString: String = "id=" + id + " distanceFromGoal=" + estDistanceFromGoal
}

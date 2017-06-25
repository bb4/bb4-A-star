// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

/**
  * @author Barry Becker
  */
class StubState {

  private var id: String = _
  private[search] var estDistanceFromGoal: Int = 0

  /** @param id some unique identifier */
  def this(id: String) {
    this()
    this.id = id
  }

  /** @param id some unique identifier*/
  def this(id: Int) {
    this()
    this.id = Integer.toString(id)
  }

  /**
    * @param id     some unique identifier
    * @param estDistanceFromGoal the estimated distance to goal should be an optimistic estimate in order to
    *                    be admissible by A*.
    */
  def this(id: String, estDistanceFromGoal: Int) {
    this(id)
    this.estDistanceFromGoal = estDistanceFromGoal
  }

  private[search] def getDistanceFromGoal: Int = estDistanceFromGoal

  override def toString: String = "id=" + id + " distanceFromGoal=" + estDistanceFromGoal

  override def equals(other: Any): Boolean = {
    other match {
      case other: StubState => id == other.id
      case _ => false
    }
  }

  override def hashCode: Int = id.hashCode
}

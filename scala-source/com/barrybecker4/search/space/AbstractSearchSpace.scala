// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.space

import scala.collection.mutable

/**
  * Describes the search space.
  * S represents a state within the search space
  * T represents a transition from one state to another
  * @author Barry Becker
  */
abstract class AbstractSearchSpace[S, T](val initialState: S) extends SearchSpace[S, T] {

  def alreadySeen(state: S, seen: mutable.Set[S]): Boolean = {
    if (!seen.contains(state)) {
      seen.add(state)
      false
    } else true
  }

  def getCost(transition: T): Int = 1

  def refresh(state: S, numTries: Long): Unit = {}

  def finalRefresh(path: Option[Seq[T]], state: Option[S], numTries: Long, elapsedMillis: Long): Unit = {
    // nothing by default
  }

  override def animateTransition(trans: T): S = this.transition(initialState, trans)
}

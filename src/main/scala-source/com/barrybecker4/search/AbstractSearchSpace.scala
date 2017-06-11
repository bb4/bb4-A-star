// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import scala.collection.mutable

/**
  * Describes the search space.
  *
  * @author Barry Becker
  */
abstract class AbstractSearchSpace[S, T](var initialState: S) extends SearchSpace[S, T] {

  def alreadySeen(state: S, seen: mutable.Set[S]): Boolean = {
    if (!seen.contains(state)) {
      seen.add(state)
      false
    } else true
  }

  def getCost(transition: T): Int = 1

  def refresh(state: S, numTries: Long) {}

  def finalRefresh(path: Option[Seq[T]], state: Option[S], numTries: Long, elapsedMillis: Long) {
    // nothing by default
  }
}

// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

/**
  * A UI element that can be refreshed to show the current state, or show an animated transition to a new state.
  * @author Barry Becker
  */
trait Refreshable[S, T] {

  /**
    * Call when you want the UI to update.
    * @param state the new state to show.
    * @param numTries number of tries so far.
    */
  def refresh(state: S, numTries: Long): Unit

  /**
    * Call when you want the UI to show an animated transition to a new state
    * @param transition describes the transition to the new state
    * @return the new state
    */
  def animateTransition(transition: T): S

  /**
    * Show the path to the goal state at the end.
    * @param path          list of transitions that gets to the solution. If path is null then no solution was found.
    * @param state         the final state in the path. It may be null if no solution was found.
    * @param numTries      number of tries it took to find that final state.
    * @param elapsedMillis number of milliseconds it took to find the solution.
    */
  def finalRefresh(path: Option[Seq[T]], state: Option[S], numTries: Long, elapsedMillis: Long): Unit
}

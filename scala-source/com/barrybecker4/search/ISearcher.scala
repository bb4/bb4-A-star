// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

/**
  * Interface for something that can search for a solution to a problem.
  * S is a state in the search space, and T is a transition between states.
  * @author Barry Becker
  */
trait ISearcher[S, T] {

  /** @return a sequence of transitions leading from the initial state to the goal state if there is one. */
  def solve: Option[Seq[T]]

  /** @return the sequence of transitions to the solution - None until it is found */
  def getPathToSolution: Option[Seq[T]]

  /** Tell the search to stop */
  def stop(): Unit
}





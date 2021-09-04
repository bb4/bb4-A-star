// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.slidingpuzzle


trait Solver {

  protected var solutionTransitions: Option[Seq[Transition]] = _


  /** @return true if the initial board is solvable */
  def isSolvable: Boolean = solutionTransitions.isDefined

  /** @return min number of moves to solve initial board; -1 if unsolvable */
  def moves: Int = if (solutionTransitions.isDefined) solutionTransitions.get.size else -1

  /** @return sequence of boards in a shortest solutionTransitions; null if unsolvable */
  def getSolution(startState: Board): Iterable[Board] = {
    if (!isSolvable) return null
    var list: List[Board] = Nil
    list +:= startState
    var previous: Board = startState

    for (trans <- solutionTransitions.get) {
      val newState: Board = previous.applyTransition(trans)
      list +:= newState
      previous = newState
    }
    list.reverse
  }
}

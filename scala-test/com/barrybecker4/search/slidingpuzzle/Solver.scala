// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.slidingpuzzle


trait Solver {

  protected var solutionTransitions: Option[Seq[Transition]] = None

  /** @return true if the initial board is solvable */
  def isSolvable: Boolean = solutionTransitions.isDefined

  /** @return min number of moves to solve initial board; -1 if unsolvable */
  def moves: Int = solutionTransitions.map(_.size).getOrElse(-1)

  /** @return sequence of boards in a shortest solution, or None if unsolvable */
  def getSolution(startState: Board): Option[Iterable[Board]] =
    solutionTransitions.map { transitions =>
      val boards = List.newBuilder[Board]
      var previous: Board = startState
      boards += startState
      for (trans <- transitions) {
        val newState: Board = previous.applyTransition(trans)
        boards += newState
        previous = newState
      }
      boards.result()
    }
}

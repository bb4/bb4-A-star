/*
 * Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.search.slidingpuzzle

import com.barrybecker4.search.space.SearchSpace
import com.barrybecker4.search.AStarSearch
import com.barrybecker4.search.queue.{HeapPriorityQueue, UpdatablePriorityQueue}

/**
  * Solver for slider-puzzle games.
  * This puzzle serves as a way of testing the generic A-star implementation.
  * @author Barry Becker
  */
object Solver {

  /** solve a slider puzzle */
  def main(args: Array[String]): Unit =  {
    val initial: Board = new BoardReader("/cases").read(args(0))
    // solve the puzzle
    val solver: Solver = new Solver(initial, new HeapPriorityQueue[Board, Transition])
    // print solutionTransitions to standard output
    if (!solver.isSolvable) System.out.println("No solutionTransitions possible")
    else {
      System.out.println("Minimum number of moves = " + solver.moves)
      for (board <- solver.solution) println(board)
    }
  }
}

class Solver(var startState: Board, val queue: UpdatablePriorityQueue[Board, Transition]) {

  // find a solution to the initial board (using the A* algorithm)
  solveAssumingSolvable(startState, queue)
  private var solutionTransitions: Option[Seq[Transition]] = _

  /** this is faster and simpler if we know its solvable */
  private def solveAssumingSolvable(initial: Board, queue: UpdatablePriorityQueue[Board, Transition]): Unit = {
    val space: SearchSpace[Board, Transition] = new PuzzleSearchSpace(initial)
    val searcher: AStarSearch[Board, Transition] = new AStarSearch[Board, Transition](space, queue)
    searcher.solve
    solutionTransitions = searcher.getSolution
  }

  /** @return true if the initial board is solvable */
  def isSolvable: Boolean = solutionTransitions.isDefined

  /** @return min number of moves to solve initial board; -1 if unsolvable */
  def moves: Int = if (solutionTransitions.isDefined) solutionTransitions.get.size else -1

  /** @return sequence of boards in a shortest solutionTransitions; null if unsolvable */
  def solution: Iterable[Board] = {
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

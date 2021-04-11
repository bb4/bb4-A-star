/*
 * Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.search.slidingpuzzle

import com.barrybecker4.search.AStarSearch
import com.barrybecker4.search.queue.{HeapPriorityQueue, UpdatablePriorityQueue}
import com.barrybecker4.search.space.SearchSpace

/**
  * Solver for slider-puzzle games.
  * This puzzle serves as a way of testing the generic A-star implementation.
  * @author Barry Becker
  */
object AStarSolver {

  /** solve a slider puzzle */
  def main(args: Array[String]): Unit =  {
    val initial: Board = new BoardReader("/cases").read(args(0))
    // solve the puzzle
    val solver: Solver = new AStarSolver(initial, new HeapPriorityQueue[Board, Transition])
    // print solutionTransitions to standard output
    if (!solver.isSolvable) System.out.println("No solutionTransitions possible")
    else {
      System.out.println("Minimum number of moves = " + solver.moves)
      for (board <- solver.getSolution(initial)) println(board)
    }
  }
}

class AStarSolver(var startState: Board, val queue: UpdatablePriorityQueue[Board, Transition]) extends Solver {

  // find a solution to the initial board (using the A* algorithm)
  solveAssumingSolvable(startState, queue)

  /** this is faster and simpler if we know its solvable */
  private def solveAssumingSolvable(initial: Board, queue: UpdatablePriorityQueue[Board, Transition]): Unit = {
    val space: SearchSpace[Board, Transition] = new PuzzleSearchSpace(initial)
    val searcher: AStarSearch[Board, Transition] = new AStarSearch[Board, Transition](space, queue)
    searcher.solve
    solutionTransitions = searcher.getPathToSolution
  }

}

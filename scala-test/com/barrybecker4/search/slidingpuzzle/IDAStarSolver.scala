// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.slidingpuzzle

import com.barrybecker4.search.IDAStarSearch
import com.barrybecker4.search.space.SearchSpace


/**
  * Solver for slider-puzzle games.
  * This puzzle serves as a way of testing the generic A-star implementation.
  * @author Barry Becker
  */
object IDAStarSolver {

  /** solve a slider puzzle */
  def main(args: Array[String]): Unit =  {
    val initial: Board = new BoardReader("/cases").read(args(0))
    // solve the puzzle
    val solver: Solver = new IDAStarSolver(initial)
    // print solutionTransitions to standard output
    if (!solver.isSolvable) System.out.println("No solutionTransitions possible")
    else {
      System.out.println("Minimum number of moves = " + solver.moves)
      for (board <- solver.getSolution(initial)) println(board)
    }
  }
}

class IDAStarSolver(var startState: Board) extends Solver {

  // find a solution to the initial board (using the A* algorithm)
  solveAssumingSolvable(startState)

  /** this is faster and simpler if we know its solvable */
  private def solveAssumingSolvable(initial: Board): Unit = {
    val space: SearchSpace[Board, Transition] = new PuzzleSearchSpace(initial)
    val searcher: IDAStarSearch[Board, Transition] = new IDAStarSearch[Board, Transition](space)
    searcher.solve
    solutionTransitions = searcher.getPathToSolution
  }
}

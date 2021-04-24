// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.slidingpuzzletests

import com.barrybecker4.search.Watch
import com.barrybecker4.search.queue.HeapPriorityQueue
import com.barrybecker4.search.slidingpuzzle.{AStarSolver, Board, Solver, Transition}

/**
  * @author Barry Becker
  */
class AStarSolverSuite extends SolverSuite {

  def createSolver(initial: Board): Solver = new AStarSolver(initial, new HeapPriorityQueue[Board, Transition])

  test("SolveHard") {
    verifyHardSolved(9)
  }

  // A* can determine that these are unsolvable. IDA* takes too long to do it.
  test("run3x3UnsolvableTestFiles") {
    val testCases: List[Case] = List(
      new Case("puzzle3x3-unsolvable.txt", -1, false),
      new Case("puzzle3x3-unsolvable1.txt", -1, false),
      new Case("puzzle3x3-unsolvable2.txt", -1, false)
    )
    runCases(testCases, 32.0)
  }
}
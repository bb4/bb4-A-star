// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.slidingpuzzletests

import com.barrybecker4.search.Watch
import com.barrybecker4.search.queue.HeapPriorityQueue
import com.barrybecker4.search.slidingpuzzle.{AStarSolver, Board, IDAStarSolver, Solver, Transition}

/**
  * @author Barry Becker
  */
class IDAStarSolverSuite extends SolverSuite {

  def createSolver(initial: Board): Solver = new IDAStarSolver(initial)

  /* not working yet
  test("SolveHard") {
    verifyHardSolved(100)
  }*/
}
// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.slidingpuzzletests

import com.barrybecker4.search.slidingpuzzle.{Board, IDAStarSolver, Solver}

/**
  * @author Barry Becker
  */
class IDAStarSolverSuite extends SolverSuite {

  def createSolver(initial: Board): Solver = new IDAStarSolver(initial)

  /* not working within time limit yet
  test("SolveHard") {
    verifyHardSolved(200)
  }*/
}
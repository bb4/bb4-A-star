package com.barrybecker4.search.slidingpuzzletests

import com.barrybecker4.common.Watch
import com.barrybecker4.search.HeapPriorityQueue
import com.barrybecker4.search.slidingpuzzle.{Board, Solver, Transition}

/**
  * @author Barry Becker
  */
class HeapQSolverSuite extends SolverSuite {
  def createSolver(initial: Board): Solver = new Solver(initial, new HeapPriorityQueue[Board, Transition])

  test("SolveMedium") {
    val testNum: Int = 11
    val file: String = "puzzle" + testNum + ".txt"
    val initial: Board = reader.read(file)
    val timer: Watch = new Watch
    solver = createSolver(initial)
    val elapsed: Double = timer.getElapsedTime
    System.out.println("elapsed = " + elapsed + " seconds.")
    assertResult( testNum, "Unexpected number of moves for " + file) { solver.moves }
    assert(solver.isSolvable, file + " unexpectedly not solvable")
    assert(elapsed < 10.0, "Took too long " + elapsed)
  }
}
// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.slidingpuzzletests

import com.barrybecker4.search.Watch
import com.barrybecker4.search.slidingpuzzle.{Board, BoardReader, Solver}
import org.scalatest.funsuite.AnyFunSuite

/**
  * @author Barry Becker
  */
abstract class SolverSuite extends AnyFunSuite {

  protected def reader: BoardReader = new BoardReader("cases/")

  protected def createSolver(initial: Board): Solver

  test("Solve8") {
    val initial: Board = reader.read("puzzle08.txt")
    val solver = createSolver(initial)
    assertResult(8, "Unexpected number of moves for puzzle8.txt") { solver.moves }
    assertResult(true, "Unexpectedly not solvable") { solver.isSolvable }
  }

  test("Solve10by10inGoalState") {
    val file: String = "puzzle00.txt"
    val initial: Board = reader.read(file)
    val solver = createSolver(initial)
    assertResult(0, "Unexpected number of moves for " + file) { solver.moves }
    assertResult(true, file + " unexpectedly not solvable") { solver.isSolvable }
  }

  test("Solve07") {
    val initial: Board = reader.read("puzzle07.txt")
    val solver = createSolver(initial)
    assertResult(7, "Unexpected number of moves for puzzle07.txt") { solver.moves }
    val path: String = getSolutionSequence(solver.getSolution(initial).get)
    assert(
      path == "3\n" + " 1  2  3 \n" + " 0  7  6 \n" + " 5  4  8 \n" + "3\n" + " 1  2  3 \n" + " 7  0  6 \n" + " 5  4  8 \n" + "3\n" + " 1  2  3 \n" + " 7  4  6 \n" + " 5  0  8 \n" + "3\n" + " 1  2  3 \n" + " 7  4  6 \n" + " 0  5  8 \n" + "3\n" + " 1  2  3 \n" + " 0  4  6 \n" + " 7  5  8 \n" + "3\n" + " 1  2  3 \n" + " 4  0  6 \n" + " 7  5  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 7  0  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 7  8  0 \n" ||
      path == "3\n" + " 1  2  3 \n" + " 0  7  6 \n" + " 5  4  8 \n" + "3\n" + " 1  2  3 \n" + " 5  7  6 \n" + " 0  4  8 \n" + "3\n" + " 1  2  3 \n" + " 5  7  6 \n" + " 4  0  8 \n" + "3\n" + " 1  2  3 \n" + " 5  0  6 \n" + " 4  7  8 \n" + "3\n" + " 1  2  3 \n" + " 0  5  6 \n" + " 4  7  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 0  7  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 7  0  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 7  8  0 \n"
    )
  }

  test("11") {
    doRun(11, 2.0)
  }

  test("SolveMedium") {
    val testNum: Int = 11
    val file: String = "puzzle" + testNum + ".txt"
    val initial: Board = reader.read(file)
    val (elapsed, solver) = timedSolve(initial)
    System.out.println("elapsed = " + elapsed + " seconds.")
    assertResult(testNum, "Unexpected number of moves for " + file) { solver.moves }
    assert(solver.isSolvable, file + " unexpectedly not solvable")
    assert(elapsed < 10.0, "Took too long " + elapsed)
  }

  def verifyHardSolved(timeLimit: Double = 10.0): Unit = {
    val file: String = "puzzle4x4-hard1.txt"
    val initial: Board = reader.read(file)
    val (elapsed, solver) = timedSolve(initial)
    System.out.println("elapsed = " + elapsed + " seconds.")
    assertResult(38, "Unexpected number of moves for " + file) { solver.moves }
    assertResult(true, file + " unexpectedly not solvable") { solver.isSolvable }
    assert(elapsed < timeLimit, "Took too long " + elapsed)
  }

  test("runAllSolvableTestFiles") {
    val testCases: List[Case] =
      Case("puzzle00.txt", 0, true) ::
        (1 to 31).map { i =>
          // should go to 49 and still be under 30s
          val filename = if (i < 10) s"puzzle0$i.txt" else s"puzzle$i.txt"
          Case(filename, i, true)
        }.toList
    runCases(testCases, 30.0)
  }

  test("run2.2UnsolvableTestFiles") {
    val testCases: List[Case] = List(
      Case("puzzle2x2-unsolvable1.txt", -1, false),
      Case("puzzle2x2-unsolvable2.txt", -1, false),
      Case("puzzle2x2-unsolvable3.txt", -1, false),
    )
    runCases(testCases, 12.0)
  }

  test("run2by2Cases") {
    val testCases: List[Case] = List(
      Case("puzzle2x2-solvable1.txt", 4, true),
      Case("puzzle2x2-solvable2.txt", 4, true)
    )
    runCases(testCases, 0.5)
  }

  private def getSolutionSequence(seq: Iterable[Board]): String =
    seq.map(_.toString).mkString

  /** Runs [[createSolver]] and returns elapsed seconds plus the solver instance. */
  private def timedSolve(initial: Board): (Double, Solver) = {
    val timer: Watch = new Watch
    val s = createSolver(initial)
    (timer.getElapsedSeconds, s)
  }

  private def doRun(testNum: Int, timeLimit: Double): Unit = {
    val file: String = "puzzle" + testNum + ".txt"
    val initial: Board = reader.read(file)
    val (elapsed, solver) = timedSolve(initial)
    System.out.println("elapsed = " + elapsed + " seconds.")
    assertResult(testNum, "Unexpected number of moves for " + file) { solver.moves }
    assert(solver.isSolvable, file + " unexpectedly not solvable")
    assert(elapsed < timeLimit, "Took too long " + elapsed)
  }

  protected def runCases(testCases: List[Case], timeLimitSecs: Double): Unit = {
    val timer: Watch = new Watch
    for (testCase <- testCases) {
      runCase(testCase)
    }
    val elapsed: Double = timer.getElapsedSeconds
    System.out.println("Elapsed time = " + elapsed + " seconds.")
    assert(elapsed < timeLimitSecs, "Took too long: " + elapsed + "seconds. Wanted " + timeLimitSecs)
  }

  private def runCase(testCase: Case): Unit = {
    val initial: Board = reader.read(testCase.filename)
    val solver = createSolver(initial)
    assertResult(testCase.expNumMoves, "Unexpected number of moves for " + testCase.filename) { solver.moves }
    if (testCase.expIsSolvable) {
      assert(solver.isSolvable, "Unexpectedly not solvable")
      solver.getSolution(initial).get.foreach(System.out.println)
    }
    else {
      assert(!solver.isSolvable, "Unexpectedly solvable")
      assert(solver.getSolution(initial).isEmpty, "Solution unexpectedly defined")
    }
  }
}

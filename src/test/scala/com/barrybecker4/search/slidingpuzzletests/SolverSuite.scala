package com.barrybecker4.search.slidingpuzzletests

import com.barrybecker4.search.Watch
import com.barrybecker4.search.slidingpuzzle.{Board, BoardReader, Solver}
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
  * @author Barry Becker
  */
abstract class SolverSuite extends FunSuite with BeforeAndAfter {
  protected var solver: Solver = _
  protected var reader: BoardReader = _

  before {
    reader = new BoardReader
  }

  protected def createSolver(initial: Board): Solver

  test("Solve8") {
    val initial: Board = reader.read("puzzle08.txt")
    solver = createSolver(initial)
    assertResult(8, "Unexpected number of moves for puzzle8.txt") {solver.moves}
    assertResult(true, "Unexpectedly not solvable") { solver.isSolvable }
  }

  test("Solve10by10inGoalState") {
    val file: String = "puzzle00.txt"
    val initial: Board = reader.read(file)
    solver = createSolver(initial)
    assertResult(0, "Unexpected number of moves for " + file) { solver.moves }
    assertResult(true, file + " unexpectedly not solvable") { solver.isSolvable }
  }

  test("Solve07") {
    val initial: Board = reader.read("puzzle07.txt")
    solver = createSolver(initial)
    assertResult(7, "Unexpected number of moves for puzzle07.txt") { solver.moves }
    val path: String = getSolutionSequence(solver.solution)
    assert(
      path == "3\n" + " 1  2  3 \n" + " 0  7  6 \n" + " 5  4  8 \n" + "3\n" + " 1  2  3 \n" + " 7  0  6 \n" + " 5  4  8 \n" + "3\n" + " 1  2  3 \n" + " 7  4  6 \n" + " 5  0  8 \n" + "3\n" + " 1  2  3 \n" + " 7  4  6 \n" + " 0  5  8 \n" + "3\n" + " 1  2  3 \n" + " 0  4  6 \n" + " 7  5  8 \n" + "3\n" + " 1  2  3 \n" + " 4  0  6 \n" + " 7  5  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 7  0  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 7  8  0 \n" ||
      path == "3\n" + " 1  2  3 \n" + " 0  7  6 \n" + " 5  4  8 \n" + "3\n" + " 1  2  3 \n" + " 5  7  6 \n" + " 0  4  8 \n" + "3\n" + " 1  2  3 \n" + " 5  7  6 \n" + " 4  0  8 \n" + "3\n" + " 1  2  3 \n" + " 5  0  6 \n" + " 4  7  8 \n" + "3\n" + " 1  2  3 \n" + " 0  5  6 \n" + " 4  7  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 0  7  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 7  0  8 \n" + "3\n" + " 1  2  3 \n" + " 4  5  6 \n" + " 7  8  0 \n"
    )
  }

  test("11") {
    doRun(11, 2.0)
  }

  test("SolveHard") {
    val file: String = "puzzle4x4-hard1.txt"
    val initial: Board = reader.read(file)
    val timer: Watch = new Watch
    solver = createSolver(initial)
    val elapsed: Double = timer.getElapsedTime
    System.out.println("elapsed = " + elapsed + " seconds.")
    assertResult(38, "Unexpected number of moves for " + file) { solver.moves }
    assertResult(true, file + " unexpectedly not solvable") { solver.isSolvable }
    assert(elapsed < 10.0, "Took too long " + elapsed)
  }

  private def getSolutionSequence(seq: Iterable[Board]): String = {
    val bldr: StringBuilder = new StringBuilder
    for (b <- seq) {
      bldr.append(b.toString)
    }
    bldr.toString
  }

  test("runAllSolvableTestFiles") {
    var testCases: List[Case] = Nil
    testCases +:= new Case("puzzle00.txt", 0, true)
    var i: Int = 1
    for (i <- 1 to 31) {
        // should go to 49 and still be under 30s
        var filename: String = "puzzle"
        if (i < 10) filename += "0"
        testCases +:= new Case(filename + i + ".txt", i, true)
    }
    runCases(testCases, 30.0)
  }

  test("runAllUnsolvableTestFiles") {
    val testCases: List[Case] = List(
      new Case("puzzle2x2-unsolvable1.txt", -1, false),
      new Case("puzzle2x2-unsolvable2.txt", -1, false),
      new Case("puzzle2x2-unsolvable3.txt", -1, false),
      new Case("puzzle3x3-unsolvable.txt", -1, false),
      new Case("puzzle3x3-unsolvable1.txt", -1, false),
      new Case("puzzle3x3-unsolvable2.txt", -1, false)
      )
    runCases(testCases, 3.0)
  }

  test("run2by2Cases") {
    val testCases: List[Case] = List(
      new Case("puzzle2x2-solvable1.txt", 4, true),
      new Case("puzzle2x2-solvable2.txt", 4, true)
    )
    runCases(testCases, 0.5)
  }

  private def doRun(testNum: Int, timeLimit: Double) {
    val file: String = "puzzle" + testNum + ".txt"
    val initial: Board = reader.read(file)
    val timer: Watch = new Watch
    solver = createSolver(initial)
    val elapsed: Double = timer.getElapsedTime
    System.out.println("elapsed = " + elapsed + " seconds.")
    assertResult(testNum, "Unexpected number of moves for " + file) { solver.moves }
    assert(solver.isSolvable, file + " unexpectedly not solvable")
    assert(elapsed < timeLimit, "Took too long " + elapsed)
  }

  protected def runCases(testCases: List[Case], timeLimitSecs: Double) {
    val timer: Watch = new Watch
    for (testCase <- testCases) {
      runCase(testCase)
    }
    val elapsed: Double = timer.getElapsedTime
    System.out.println("Elapsed time = " + elapsed + " seconds.")
    assert(elapsed < timeLimitSecs, "Took too long: " + elapsed + "seconds. Wanted " + timeLimitSecs)
    assert(elapsed > (timeLimitSecs / 1000.0), "TOO FAST!?!: " + elapsed + "seconds.")
  }

  private def runCase(testCase: Case) {
    System.out.println(testCase.filename)
    val initial: Board = reader.read(testCase.filename)
    solver = createSolver(initial)
    assertResult(testCase.expNumMoves, "Unexpected number of moves for " + testCase.filename) { solver.moves }
    if (testCase.expIsSolvable) {
      assert(solver.isSolvable, "Unexpectedly not solvable")
      val sol: Iterable[Board] = solver.solution
      for (b <- sol) {
        System.out.println(b)
      }
    }
    else {
      assert(!solver.isSolvable, "Unexpectedly solvable")
      assert(solver.solution == null, "Solution not null")
    }
  }
}

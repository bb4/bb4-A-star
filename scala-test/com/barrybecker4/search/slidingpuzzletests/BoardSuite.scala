// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.slidingpuzzletests

import com.barrybecker4.search.Location
import com.barrybecker4.search.slidingpuzzle.{Board, Transition}
import org.scalatest.funsuite.AnyFunSuite

/**
  * @author Barry Becker
  */
class BoardSuite extends AnyFunSuite {
  private val SOLVED_3: Array[Array[Int]] = Array[Array[Int]](Array(1, 2, 3), Array(4, 5, 6), Array(7, 8, 0))
  private val ALMOST_SOLVED_3: Array[Array[Int]] = Array[Array[Int]](Array(1, 2, 3), Array(4, 5, 6), Array(7, 0, 8))
  private val SPACE_TOP_LEFT: Array[Array[Int]] = Array[Array[Int]](Array(0, 2, 3), Array(4, 5, 6), Array(7, 1, 8))
  private val SPACE_TOP_MIDDLE: Array[Array[Int]] = Array[Array[Int]](Array(2, 0, 3), Array(4, 5, 6), Array(7, 1, 8))
  private val SPACE_BOTTOM_RIGHT: Array[Array[Int]] = Array[Array[Int]](Array(1, 5, 3), Array(4, 2, 6), Array(7, 8, 0))
  private val SPACE_IN_MIDDLE: Array[Array[Int]] = Array[Array[Int]](Array(1, 2, 3), Array(4, 0, 6), Array(7, 5, 8))
  private val RANDOM_BOARD: Array[Array[Int]] = Array[Array[Int]](Array(3, 2, 6), Array(8, 0, 1), Array(7, 5, 4))

  test("Dimension") {
    val board = new Board(ALMOST_SOLVED_3)
    assertResult(3) { board.dimension }
  }

  test("Equality") {
    val board = new Board(ALMOST_SOLVED_3)
    val board2: Board = new Board(ALMOST_SOLVED_3)
    assertResult(board2) { board }
  }

  test("Inequality") {
    val board = new Board(ALMOST_SOLVED_3)
    val board2: Board = new Board(SPACE_TOP_LEFT)
    assert(!board2.equals(board))
  }

  test("TwinSpaceOnFirstRow") {
    val board = new Board(SPACE_TOP_MIDDLE)
    val twin: Board = board.twin
    assertResult("3\n" + " 2  0  3 \n" + " 5  4  6 \n" + " 7  1  8 \n") {twin.toString }
  }

  test("TwinSpaceOnSecondRow"){
    val board = new Board(RANDOM_BOARD)
    val twin: Board = board.twin
    assertResult("3\n" + " 2  3  6 \n" + " 8  0  1 \n" + " 7  5  4 \n"){ twin.toString}
  }

  test("BoardIsNotSolved")  {
    val board = new Board(ALMOST_SOLVED_3)
    assertResult(1, "Unexpected hamming distance") { board.hamming }
    assert(!board.isGoal)
  }

  test("BoardIsSolved") {
    val board = new Board(SOLVED_3)
    assertResult(0, "Unexpected hamming distance") { board.hamming }
    assert(board.isGoal)
  }

  test("OneStepDistance") {
    val board = new Board(ALMOST_SOLVED_3)
    assertResult(1, "Unexpected hamming distance"){ board.hamming }
    assertResult(1, "Unexpected manhattan distance") { board.manhattanDistance }
  }

  test("ApplyTransition") {
    val board = new Board(ALMOST_SOLVED_3)
    val trans1: Transition = Transition(Location(2, 1), Location(1, 1))
    var newBoard: Board = board.applyTransition(trans1)
    assertResult(2, "Unexpected hamming distance after trans1") { newBoard.hamming }
    assertResult("3\n" + " 1  2  3 \n" + " 4  0  6 \n" + " 7  5  8 \n") { newBoard.toString }
    val trans2: Transition = Transition(Location(1, 1), Location(1, 0))
    newBoard = newBoard.applyTransition(trans2)
    assertResult(3, "Unexpected hamming distance after trans2") { newBoard.hamming }
    assertResult("3\n" + " 1  2  3 \n" + " 0  4  6 \n" + " 7  5  8 \n") { newBoard.toString }
  }

  test("FindNeighbors") {
    assertResult(2, "Unexpected num neighbors for top left") { new Board(SPACE_TOP_LEFT).neighbors.size }
    assertResult(3, "Unexpected num neighbors for top middle") { new Board(SPACE_TOP_MIDDLE).neighbors.size }
    assertResult(2, "Unexpected num neighbors for bottom right") { new Board(SPACE_BOTTOM_RIGHT).neighbors.size }
    assertResult(4, "Unexpected num neighbors for middle") { new Board(SPACE_IN_MIDDLE).neighbors.size }
  }

  test("neighbors are ordered by non-decreasing Manhattan distance") {
    val board = new Board(RANDOM_BOARD)
    val manhattans = board.neighbors.map(_.manhattanDistance).toList
    assert(manhattans == manhattans.sorted, "neighbors should be sorted by manhattan distance")
  }

  test("FindTopLeftNeighbors") {
    val board = new Board(SPACE_TOP_LEFT)
    assertResult("List(3\n" + " 4  2  3 \n" + " 0  5  6 \n" + " 7  1  8 \n" + ", 3\n" + " 2  0  3 \n" + " 4  5  6 \n" + " 7  1  8 \n" + ")") {
      board.neighbors.toString
    }
  }
}

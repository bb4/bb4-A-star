// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import com.barrybecker4.search.space.{AbstractSearchSpace, StubSearchSpace}
import com.barrybecker4.search.slidingpuzzle.{BoardReader, PuzzleSearchSpace, Transition}
import org.scalatest.funsuite.AnyFunSuite

/**
  * When the heuristic is admissible and IDA* weight is 100%, A* and IDA* must agree on total path cost.
  */
class IDAStarAStarParitySuite extends AnyFunSuite {

  private def pathCost[S, T](space: com.barrybecker4.search.space.SearchSpace[S, T],
                             path: Seq[T]): Int =
    path.map(space.getCost).sum

  private def assertSameCost[S, T](space: com.barrybecker4.search.space.SearchSpace[S, T]): Unit = {
    val aStar = new AStarSearch[S, T](space)
    val ida = new IDAStarSearch[S, T](space, IDAStarSearchConfig())
    val aPath = aStar.solve.get
    val iPath = ida.solve.get
    assert(pathCost(space, aPath) == pathCost(space, iPath),
      s"A* cost ${pathCost(space, aPath)} != IDA* cost ${pathCost(space, iPath)}; paths $aPath vs $iPath")
  }

  /** Linear chain used by [[IDAStarSearchSuite]] */
  private class LinearSolvableSpace extends AbstractSearchSpace[String, String]("0") {
    override def isGoal(state: String): Boolean = state == "goal"

    override def legalTransitions(state: String): Seq[String] = state match {
      case "0" => Seq("to1")
      case "1" => Seq("togoal")
      case _ => Seq.empty
    }

    override def transition(state: String, t: String): String = t match {
      case "to1" => "1"
      case "togoal" => "goal"
      case _ => state
    }

    override def distanceFromGoal(state: String): Int = state match {
      case "goal" => 0
      case "1" => 1
      case "0" => 2
      case _ => 10
    }
  }

  test("A* and IDA* agree on StubSearchSpace path cost") {
    assertSameCost(new StubSearchSpace)
  }

  test("A* and IDA* agree on linear solvable space path cost") {
    assertSameCost(new LinearSolvableSpace)
  }

  test("A* and IDA* agree on sliding puzzle08 path cost") {
    val board = new BoardReader("cases/").read("puzzle08.txt")
    assertSameCost(new PuzzleSearchSpace(board))
  }
}

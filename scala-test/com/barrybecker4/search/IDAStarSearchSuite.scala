// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import com.barrybecker4.search.space.AbstractSearchSpace
import org.scalatest.funsuite.AnyFunSuite

/**
  * Direct tests for [[IDAStarSearch]] (not only via sliding-puzzle integration).
  */
class IDAStarSearchSuite extends AnyFunSuite {

  /** Linear chain: 0 -> 1 -> goal */
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

  /** Two-state loop; goal is never reachable */
  private class UnsolvablePingPongSpace extends AbstractSearchSpace[String, String]("a") {
    override def isGoal(state: String): Boolean = state == "goal"

    override def legalTransitions(state: String): Seq[String] = state match {
      case "a" => Seq("toB")
      case "b" => Seq("toA")
      case _ => Seq.empty
    }

    override def transition(state: String, t: String): String = t match {
      case "toB" => "b"
      case "toA" => "a"
      case _ => state
    }

    override def distanceFromGoal(state: String): Int =
      if state == "goal" then 0 else 1
  }

  test("IDA* finds optimal transition sequence on tiny graph") {
    val searcher = new IDAStarSearch[String, String](new LinearSolvableSpace)
    val path = searcher.solve.get
    assert(path == Seq("to1", "togoal"))
  }

  test("IDA* returns None when goal is unreachable (bounded search)") {
    val searcher = new IDAStarSearch[String, String](new UnsolvablePingPongSpace)
    assert(searcher.solve.isEmpty)
  }

  test("IDA* solve can be run twice on the same searcher") {
    val space = new LinearSolvableSpace
    val searcher = new IDAStarSearch[String, String](space)
    val a = searcher.solve.get
    val b = searcher.solve.get
    assert(a == b)
  }
}

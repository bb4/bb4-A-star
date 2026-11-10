// Copyright by Barry G. Becker, 2017-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
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

  /** Deep layered DAG with zero heuristic; goal never appears. Bound rises until leaves. */
  private class DeepUnsolvableZeroHSpace(depth: Int) extends AbstractSearchSpace[Int, String](0) {
    override def isGoal(state: Int): Boolean = false

    override def legalTransitions(state: Int): Seq[String] =
      if state < depth then Seq("next") else Seq.empty

    override def transition(state: Int, t: String): Int = state + 1

    override def distanceFromGoal(state: Int): Int = 0
  }

  private class KnownUnsolvableSpace extends LinearSolvableSpace {
    override def isKnownUnsolvable: Boolean = true
  }

  /** Records whether [[finalRefresh]] was called. */
  private class SpyRefreshSpace extends LinearSolvableSpace {
    var finalRefreshCalls = 0
    override def finalRefresh(path: Option[Seq[String]], state: Option[String],
                              numTries: Long, elapsedMillis: Long): Unit =
      finalRefreshCalls += 1
  }

  /**
    * Branching space with two paths to goal: short optimal (cost 2) and long (cost 4).
    * Higher weightPercent tends to prefer deeper/greedier exploration.
    */
  private class TwoPathSpace extends AbstractSearchSpace[String, String]("s") {
    override def isGoal(state: String): Boolean = state == "g"

    override def legalTransitions(state: String): Seq[String] = state match {
      case "s" => Seq("short1", "long1")
      case "a" => Seq("short2")
      case "b" => Seq("long2")
      case "c" => Seq("long3")
      case "d" => Seq("long4")
      case _ => Seq.empty
    }

    override def transition(state: String, t: String): String = t match {
      case "short1" => "a"
      case "short2" => "g"
      case "long1" => "b"
      case "long2" => "c"
      case "long3" => "d"
      case "long4" => "g"
      case _ => state
    }

    // Admissible: underestimates remaining. From s, both paths exist; h underestimates long path.
    override def distanceFromGoal(state: String): Int = state match {
      case "g" => 0
      case "a" => 1
      case "b" => 1
      case "c" => 1
      case "d" => 1
      case "s" => 2
      case _ => 10
    }
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

  test("IDA* respects maxNodeExpansions and returns None") {
    val config = IDAStarSearchConfig(maxNodeExpansions = Some(1L))
    val searcher = new IDAStarSearch[String, String](new LinearSolvableSpace, config)
    assert(searcher.solve.isEmpty)
    assert(searcher.nodeExpansions <= 1L)
  }

  test("IDA* still calls finalRefresh when expansion cap is hit") {
    val space = new SpyRefreshSpace
    val config = IDAStarSearchConfig(maxNodeExpansions = Some(1L))
    val searcher = new IDAStarSearch[String, String](space, config)
    assert(searcher.solve.isEmpty)
    assert(space.finalRefreshCalls == 1)
  }

  test("IDA* stop() during search returns None and still calls finalRefresh") {
    class StopSpySpace extends AbstractSearchSpace[Int, String](0) {
      var refreshed = false
      var searcherRef: IDAStarSearch[Int, String] = scala.compiletime.uninitialized
      override def isGoal(state: Int): Boolean = false
      override def legalTransitions(state: Int): Seq[String] = {
        // Stop once we start expanding so solve exits cleanly mid-search
        if searcherRef != null then searcherRef.stop()
        if state < 40 then Seq("next") else Seq.empty
      }
      override def transition(state: Int, t: String): Int = state + 1
      override def distanceFromGoal(state: Int): Int = 0
      override def finalRefresh(path: Option[Seq[String]], state: Option[Int],
                                numTries: Long, elapsedMillis: Long): Unit =
        refreshed = true
    }
    val spy = new StopSpySpace
    val searcher = new IDAStarSearch[Int, String](spy, IDAStarSearchConfig())
    spy.searcherRef = searcher
    assert(searcher.solve.isEmpty)
    assert(spy.refreshed)
  }

  test("IDA* isKnownUnsolvable returns None immediately") {
    val start = System.currentTimeMillis
    val searcher = new IDAStarSearch[String, String](new KnownUnsolvableSpace)
    assert(searcher.solve.isEmpty)
    assert(searcher.nodeExpansions == 0L)
    assert(System.currentTimeMillis - start < 50)
  }

  test("IDA* finishes deep unsolvable micro-space quickly with expansion cap") {
    val start = System.currentTimeMillis
    val config = IDAStarSearchConfig(maxNodeExpansions = Some(100L))
    val searcher = new IDAStarSearch[Int, String](new DeepUnsolvableZeroHSpace(30), config)
    assert(searcher.solve.isEmpty)
    assert(searcher.nodeExpansions <= 100L)
    assert(System.currentTimeMillis - start < 500)
  }

  test("IDA* terminateWhenBoundUnchanged still finds solvable path") {
    val config = IDAStarSearchConfig(terminateWhenBoundUnchanged = true)
    val searcher = new IDAStarSearch[String, String](new LinearSolvableSpace, config)
    assert(searcher.solve.contains(Seq("to1", "togoal")))
  }

  test("IDA* with iteration transposition finds linear solution") {
    val config = IDAStarSearchConfig(useIterationTransposition = true)
    val searcher = new IDAStarSearch[String, String](new LinearSolvableSpace, config)
    assert(searcher.solve.contains(Seq("to1", "togoal")))
  }

  test("IDA* weight 100 finds optimal; higher weight finds a path of cost >= optimal") {
    val space = new TwoPathSpace
    val optimal = new IDAStarSearch[String, String](space, IDAStarSearchConfig(weightPercent = 100))
    val greedy = new IDAStarSearch[String, String](space, IDAStarSearchConfig(weightPercent = 300))
    val optPath = optimal.solve.get
    val greedyPath = greedy.solve.get
    assert(optPath == Seq("short1", "short2"))
    assert(optPath.size == 2)
    assert(greedyPath.nonEmpty)
    assert(greedyPath.size >= optPath.size)
  }

  test("IDA* maxBoundIterations constructor overload still works") {
    val searcher = new IDAStarSearch[String, String](new LinearSolvableSpace, 10)
    assert(searcher.solve.contains(Seq("to1", "togoal")))
    assert(searcher.config.maxBoundIterations == 10)
  }
}

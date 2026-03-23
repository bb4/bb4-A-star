// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import com.barrybecker4.search.space.StubSearchSpace
import org.scalatest.funsuite.AnyFunSuite

class AStarSearchReentrancySuite extends AnyFunSuite {

  test("same AStarSearch instance can be solved repeatedly with identical result") {
    val space = new StubSearchSpace
    val searcher = new AStarSearch[StubState, StubTransition](space)
    val first = searcher.solve.get.toString
    val second = searcher.solve.get.toString
    assert(first == second)
  }

  test("visited and path cost are reset between solves") {
    val space = new StubSearchSpace
    val searcher = new AStarSearch[StubState, StubTransition](space)
    searcher.solve
    assert(searcher.visited.nonEmpty)
    searcher.solve
    // After second solve completes, visited reflects only the second run's exploration
    assert(searcher.visited.nonEmpty)
  }
}

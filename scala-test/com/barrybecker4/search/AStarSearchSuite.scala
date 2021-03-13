// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import com.barrybecker4.search.space.StubSearchSpace
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite

/**
  * @author Barry Becker
  */

class AStarSearchSuite extends AnyFunSuite with BeforeAndAfter {

  private var space: StubSearchSpace = _

  before {
    space = new StubSearchSpace
  }

  test("StubSearchSpace distance from goal") {
    assertResult(9) { space.distanceFromGoal(space.initialState) }
    assertResult(42) { space.distanceFromGoal(StubState("D", 42)) }
  }

  /** test the protected search method to see if it finds the goal state */
  test("solve simple case") {
    val searcher: AStarSearch[StubState, StubTransition] = new AStarSearch[StubState, StubTransition](space)

    // a list of transitions to the goal state
    val path: Option[Seq[StubTransition]] = searcher.solve

    assertResult("List([id=D distanceFromGoal=5 cost=4], [id=E distanceFromGoal=4 cost=6], [id=goal distanceFromGoal=0 cost=4])") {
      path.get.toString
    }
  }
}

// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import scala.collection.mutable

/**
  * A fake, very simple, example search problem for use in unit tests.
  * The StubNodes define a network, with costs on edges, that can be searched by A*.
  * See https://en.wikipedia.org/wiki/A*_search_algorithm (where I get this example)
  *
  * "start"
  * / (3)     \ (4)
  * A            D
  * | (4)        | (6)
  * B            E
  * | (6)        | (4)
  * C ---(8)---  "goal"
  *
  * @author Barry Becker
  */
class StubProblem {

  val stateA: StubState = StubState("A", 8)
  val stateB: StubState = StubState("B", 7)
  val stateC: StubState = StubState("C", 6)
  val stateD: StubState = StubState("D", 5)
  val stateE: StubState = StubState("E", 4)
  private val goalState: StubState = StubState("goal", 0)
  private val initialState: StubState = StubState("start", 9)

  val transitionMap: mutable.Map[StubState, Seq[StubTransition]] = mutable.Map(
    initialState-> Seq(StubTransition(stateA, 3), StubTransition(stateD, 4)),
    stateA -> Seq(StubTransition(stateB, 4)),
    stateB -> Seq(StubTransition(stateC, 6)),
    stateC -> Seq(StubTransition(goalState, 8), StubTransition(stateB, 8)),
    stateD -> Seq(StubTransition(stateE, 6)),
    stateE -> Seq(StubTransition(goalState, 4), StubTransition(stateD, 6)),
    goalState ->Seq(StubTransition(stateE, 4), StubTransition(stateC, 8))
  )

  def getInitialState: StubState = initialState

  def getGoalState: StubState = goalState

  def getLegalTransitions(state: StubState): Seq[StubTransition] = transitionMap(state)
}

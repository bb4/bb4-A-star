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

  val stateA: StubState = new StubState("A", 8)
  val stateB: StubState = new StubState("B", 7)
  val stateC: StubState = new StubState("C", 6)
  val stateD: StubState = new StubState("D", 5)
  val stateE: StubState = new StubState("E", 4)
  private val goalState: StubState = new StubState("goal", 0)
  private val initialState: StubState = new StubState("start", 9)

  val transitionMap: mutable.Map[StubState, Seq[StubTransition]] = mutable.Map(
    initialState-> Seq(new StubTransition(stateA, 3), new StubTransition(stateD, 4)),
    stateA -> Seq(new StubTransition(stateB, 4)),
    stateB -> Seq(new StubTransition(stateC, 6)),
    stateC -> Seq(new StubTransition(goalState, 8), new StubTransition(stateB, 8)),
    stateD -> Seq(new StubTransition(stateE, 6)),
    stateE -> Seq(new StubTransition(goalState, 4), new StubTransition(stateD, 6)),
    goalState ->Seq(new StubTransition(stateE, 4), new StubTransition(stateC, 8))
  )

  def getInitialState: StubState = initialState

  def getGoalState: StubState = goalState

  def getLegalTransitions(state: StubState): Seq[StubTransition] = transitionMap(state)
}

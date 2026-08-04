// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.space

/**
  * A search space consisting of only 3 possible states: start, intermediate and goal.
  * @author Barry Becker
  */
class TrivialSearchSpace extends AbstractSearchSpace[String, (String, String)]("start") {

  private val nextStates = Map("start" -> "intermediate", "intermediate" -> "goal")

  override def isGoal(state: String): Boolean = state == "goal"

  override def legalTransitions(state: String): Seq[(String, String)] = Seq((state, nextStates(state)))

  override def transition(state: String, transition: (String, String)): String = transition._2

  override def distanceFromGoal(state: String): Int = 1
}

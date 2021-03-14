// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.space

import scala.collection.mutable

/**
  * A search space consisting of only 3 possible states: start, intermediate and goal.
  * @author Barry Becker
  */
class TrivialSearchSpace extends SearchSpace[String, (String, String)] {

    private val nextStates = Map("start" -> "intermediate", "intermediate" -> "goal")

    override def initialState: String = "start"
    override def isGoal(state: String): Boolean = state == "goal"
    override def legalTransitions(state: String): Seq[(String, String)] = Seq((state, nextStates(state)))
    override def transition(state: String, transition: (String, String)): String = transition._2
    override def alreadySeen(state: String, seen: mutable.Set[String]): Boolean = false
    override def distanceFromGoal(state: String): Int = 1
    override def getCost(transition: (String, String)): Int = 1
    override def refresh(state: String, numTries: Long): Unit = {}
    override def finalRefresh(path: Option[Seq[(String, String)]],
                              state: Option[String], numTries: Long, elapsedMillis: Long): Unit = {}
    override def animateTransition(trans: (String, String)): String = trans._2
}

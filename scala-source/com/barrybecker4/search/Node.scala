// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

/**
  * Represents a state and how we got to it from the last state. Immutable
  * Link node for a state in the global search space.
  * Contains an immutable state S and an immutable transition T that got us to this state from the last one.
  * The estimated future cost is used for sorting nodes.
  *
  * @param state the current state in the global search space
  * @param transition the transformation that got to this state
  * @param previous the previous state
  * @param pathCost cost from initial position to the state represented by this node
  * @param estimatedFutureCost the cost of getting here plus the estimated future cost to get to the goal.
  *    IOW, an estimate of the total eventual cost of the path from start to goal running through this node.
  * @author Barry Becker
  */
class Node[S, T](val state: S, val transition: Option[T] = None,
                 var previous: Option[Node[S, T]] = None,
                 val pathCost: Int = 0, val estimatedFutureCost: Int = 1)
  extends Comparable[Node[S, T]] {

  /** Use this only when there is no transition to this node.
    * @param initialState        initial state
    * @param estFutureCost the cost of getting here plus the estimated future cost to get to the finish.
    */
  def this(initialState: S, estFutureCost: Int) {
    this(initialState, estimatedFutureCost = estFutureCost)
  }

  def compareTo(otherNode: Node[S, T]): Int = estimatedFutureCost - otherNode.estimatedFutureCost

  override def equals(other: Any): Boolean = {
    other match {
      case other: Node[S, T] => state == other.state
      case _ => false
    }
  }

  override def hashCode: Int = state.hashCode

  /** @return a list of nodes from the start state to this state. */
  def asTransitionList: Seq[T] = {
    var solution: List[T] = List()
    var node: Node[S, T] = this
    while (node.transition.isDefined) {
      solution = node.transition.get +: solution
      node = node.previous.get
    }
    solution
  }

  override def toString: String = "[" + state + ", pathCost=" + pathCost + " totalCost=" + estimatedFutureCost + "]"
}
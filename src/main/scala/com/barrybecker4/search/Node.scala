/** Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.search

/**
  * Link node for a state in the global search space.
  * Contains an immutable state and a transition that got us to this state from the last one.
  * The estimated future cost is used for sorting nodes.
  * S is the state type
  * T is the transition from one state to the next.
  *
  * Represents a state and how we got to it from the last state. Immutable
  *
  * @param state               the current state state
  * @param transition          the transformation that got to this state
  * @param previous                the previous state
  * @param pathCost            cost from initial position to the state represented by this node
  * @param estimatedFutureCost the cost of getting here plus the estimated future cost to get to the finish.
  *           IOW, an estimate of the total eventual cost of the path from start to goal running through this node
  * @author Barry Becker
  */
class Node[S, T](val state: S, val transition: Option[T] = None,
                 var previous: Option[Node[S, T]] = None,
                 val pathCost: Int = 0, val estimatedFutureCost: Int = 1)
  extends Comparable[Node[S, T]] {

  /**
    * Use this only when there is no transition to this node.
    *
    * @param initialState        initial state
    * @param estFutureCost the cost of getting here plus the estimated future cost to get to the finish.
    */
  def this(initialState: S, estFutureCost: Int) {
    this(initialState, estimatedFutureCost = estFutureCost)
  }

  /** @return state in the global search space */
  def getState: S = state

  def getPathCost: Int = pathCost

  /** @return An estimate of how much it will cost to go from this state to the goal state */
  def getEstimatedFutureCost: Int = estimatedFutureCost

  def getPrevious: Option[Node[S, T]] = previous

  def setPrevious(p: Node[S, T]) { previous = Some(p) }

  def compareTo(otherNode: Node[S, T]): Int = getEstimatedFutureCost - otherNode.getEstimatedFutureCost

  override def equals(other: Any): Boolean = {
    other match {
      case other: Node[S, T] => state == other.state
      case _ => false
    }
  }

  override def hashCode: Int = state.hashCode

  /**
    * @return a list of nodes from the start state to this state.
    */
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

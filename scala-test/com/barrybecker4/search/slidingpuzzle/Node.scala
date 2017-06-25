// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.search.slidingpuzzle

/**
  * Immutable Node for a state in the global search space.
  * A search node is a board, the number of moves made to reach the board, and the previous search node.
  * @param state    the current state state
  * @param previous the previous state
  * @param pathCost cost from initial position to the state represented by this node.
  * @author Barry Becker
  */
class Node(val state: Board, var previous: Node = null, val pathCost: Int = 0) extends Comparable[Node] {

  /** @return state in the global search space */
  def getState: Board = state

  def getPreviousBoard: Board = if (previous == null) null
  else previous.getState

  /** @return cost to get from the initial state to the current one. For example, number of steps. */
  def getPathCost: Int = pathCost

  def getPriority: Int = getPathCost + state._manhattan // * MULTIPLIER + state.hamming();

  override def toString: String = "[" + state + ", pathCost=" + pathCost + "]"

  def compareTo(n: Node): Int = this.getPriority - n.getPriority
}

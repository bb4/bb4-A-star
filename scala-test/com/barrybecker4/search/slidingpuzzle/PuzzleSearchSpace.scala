// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.slidingpuzzle

import com.barrybecker4.search.AbstractSearchSpace

/**
  * @author Barry Becker
  */
class PuzzleSearchSpace(initialState: Board) extends AbstractSearchSpace[Board, Transition](initialState) {
  def isGoal(state: Board): Boolean = state.hamming == 0

  def legalTransitions(state: Board): List[Transition] = state.getNeighborTransitions

  def transition(state: Board, transition: Transition): Board = state.applyTransition(transition)

  def distanceFromGoal(state: Board): Int = state._manhattan

  override def getCost(transition: Transition): Int = 1
}

package com.barrybecker4.search

/**
  * @param newState the new state to be transitioned to.
  */
class StubTransition private[search](var newState: StubState) {

  private[search] var cost: Int = 0

  /**
    * @param newState the new state to be transitioned to.
    * @param cost     the cost of making this transition
    */
  def this(newState: StubState, cost: Int) {
    this(newState)
    this.cost = cost
  }

  private[search] def getNewState: StubState = newState

  private[search] def getCost: Int = cost

  override def toString: String = "[" + newState + " cost=" + cost + "]"
}

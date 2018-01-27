// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

/**
  * @param newState the new state to be transitioned to.
  * @param cost the cost of making this transition
  * @author Barry Becker
  */
case class StubTransition private[search](newState: StubState, cost: Int = 0) {

  override def toString: String = "[" + newState + " cost=" + cost + "]"
}

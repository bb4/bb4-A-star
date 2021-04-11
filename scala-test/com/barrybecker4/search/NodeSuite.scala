package com.barrybecker4.search

import org.junit.Assert.{assertFalse, assertTrue}
import org.scalatest.funsuite.AnyFunSuite

class NodeSuite extends AnyFunSuite {

  private val prevState = StubState("Prev", 110)
  private val state = StubState("Current", 100)
  private val nextState = StubState("Next", 90)
  private val otherState = StubState("Other", 42)

  private val transition = StubTransition(state)
  private val nextTransition = StubTransition(nextState)

  val prevNode: Node[StubState, StubTransition] =
    new Node[StubState, StubTransition](prevState, None, None, 0, 95)

  val node: Node[StubState, StubTransition] =
    new Node[StubState, StubTransition](state, Some(transition), Some(prevNode), 10, 87)

  val nextNode: Node[StubState, StubTransition] =
    new Node[StubState, StubTransition](nextState, Some(nextTransition), Some(node), 11, 77)


  test("Node construction") {
    assertResult(10) { node.pathCost }
    assertResult(prevNode) { node.previous.get }
    assertResult(state) { node.state }
    assertResult(87) { node.estimatedTotalCost }
  }

  test("Convert to path (i.e. transition sequence") {
    assertResult(Seq[StubTransition](transition, nextTransition)) { nextNode.asTransitionList }
  }

  test("containsStateInPath") {
    assertTrue(nextNode.containsStateInPath(nextState))
    assertTrue(nextNode.containsStateInPath(state))
    assertTrue(nextNode.containsStateInPath(prevState))
  }

  test("does not containsStateInPath") {
    assertFalse(nextNode.containsStateInPath(otherState))
  }

  test("compareTo state") {
    assertResult(-18) { nextNode.compareTo(prevNode) }
    assertResult(18) { prevNode.compareTo(nextNode) }
    assertResult(0) { node.compareTo(node) }
    assertResult(-8) { node.compareTo(prevNode) }
    assertResult(8) { prevNode.compareTo(node) }
  }

}

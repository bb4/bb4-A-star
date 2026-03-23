package com.barrybecker4.search

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
    assert(nextNode.containsStateInPath(nextState))
    assert(nextNode.containsStateInPath(state))
    assert(nextNode.containsStateInPath(prevState))
  }

  test("does not containsStateInPath") {
    assert(!nextNode.containsStateInPath(otherState))
  }

  test("compareTo state") {
    // Uses total-cost ordering (see [[Node.compareTo]]); sign is what matters for heaps, not the magnitude.
    assert(nextNode.compareTo(prevNode) < 0)
    assert(prevNode.compareTo(nextNode) > 0)
    assert(node.compareTo(node) == 0)
    assert(node.compareTo(prevNode) < 0)
    assert(prevNode.compareTo(node) > 0)
  }

}

package com.barrybecker4.search.queue

import com.barrybecker4.search.Node
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite

class HeapPriorityQueueSuite extends AnyFunSuite with BeforeAndAfter {

  var q: HeapPriorityQueue[String, Int] = _

  before {
    q = new HeapPriorityQueue[String, Int]()
  }

  test("Add to queue") {
    val node1 = new Node[String, Int]("foo", 42)
    val node2 = new Node[String, Int]("bar", 12)

    q.add(node1)
    q.add(node2)
    assertResult(2) { q.size }
    // Note that "bar' got moved in front of "foo" because it has smaller estFutureCost.
    assertResult("[bar, pathCost=0 totalCost=12]") { q.peek.toString }

    q.pop
    assertResult(1) { q.size }
    assertResult(false) {q.isEmpty}
  }

  test("pop from queue") {
    val node1 = new Node[String, Int]("foo", 42)
    val node2 = new Node[String, Int]("bar", 12)

    q.add(node1)
    q.add(node2)
    assertResult(2) { q.size }
    q.pop
    assertResult(1) { q.size }
    q.pop
    assertResult(0) { q.size }
    assertResult(true) {q.isEmpty}
  }

  test("Error if try to pop from empty queue") {
    val node1 = new Node[String, Int]("foo", 42)
    q.add(node1)
    q.pop

    assertThrows[AssertionError] {
      q.pop
    }
  }

  test("Update in queue") {
    val q = new HeapPriorityQueue[String, Int]()

    val prevNode1 = new Node[String, Int]("prev", 42)
    val node1 = new Node[String, Int]("foo", Some(3), Some(prevNode1), 20, 12)
    val node2 = new Node[String, Int]("bar", 22)
    val node3 = new Node[String, Int]("blah", 42)

    q.add(node1)
    q.add(node2)
    q.add(node3)
    assertResult(3) { q.size }

    val node4 = new Node[String, Int]("blah", 7)
    // the updated node will move to the front of the queue
    q.addOrUpdate(node4)
    assertResult(3) { q.size }

    // Note that "bar' got moved in front of "foo" because it has smaller estFutureCost.
    assertResult("[blah, pathCost=0 totalCost=7]") { q.peek.toString }
  }
}

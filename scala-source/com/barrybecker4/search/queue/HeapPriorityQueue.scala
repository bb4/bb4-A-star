// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.queue

import com.barrybecker4.search.Node

import java.util
import java.util.Comparator
import scala.collection.mutable

/**
  * A priority queue implementation based on a binary heap.
  * It is pretty much the same as PriorityQueue provided by java
  * with two modifications:
  *  - It allows updating the priority of nodes (needed by A*)
  *  - Does not implement Queue interface since that has more methods than needed.
  * @author Josh Bloch, Doug Lea, modified by Barry Becker as indicated above.
  */
object HeapPriorityQueue {

  private val DEFAULT_INITIAL_CAPACITY: Int = 256

  /** The maximum size of the array to allocate.
    * Some VMs reserve header words in an array. Attempts to allocate larger arrays may result in OutOfMemoryError.
    */
  private val MAX_ARRAY_SIZE: Int = Int.MaxValue - 8

  private def hugeCapacity(minCapacity: Int): Int = {
    if (minCapacity < 0) throw new OutOfMemoryError
    if (minCapacity > MAX_ARRAY_SIZE) Int.MaxValue
    else MAX_ARRAY_SIZE
  }
}

/** Creates a PriorityQueue with the specified initial capacity
  * that orders its elements according to the specified comparator.
  * @param initialCapacity the initial capacity for this priority queue
  * @param comparator The comparator, or null if priority queue uses elements' natural ordering.
  * S represents a state in the global search space.
  * T represents a transition from one state to the next.
  */
class HeapPriorityQueue[S, T](val initialCapacity: Int = HeapPriorityQueue.DEFAULT_INITIAL_CAPACITY,
                              val comparator: Comparator[_ >: Node[S, T]] = null)
  extends UpdatablePriorityQueue[S, T] {

  /** Priority queue represented as a balanced binary heap.
    * The two children of queue[n] are queue[2*n+1] and queue[2*(n+1)].
    * The priority queue is ordered by comparator, or by the elements' natural ordering.
    * If comparator is null: For each node n in the heap and each descendant d of n, n <= d.
    * The element with the lowest value is in queue[0], assuming the queue is nonempty.
    */
  private var queue: Array[Node[S, T]] = new Array[Node[S, T]](initialCapacity)

  /** allows for quick lookup of a nodes position in the heap. Required for updating priority of nodes */
  private val indexMap: mutable.Map[Node[S, T], Int] = mutable.Map[Node[S, T], Int]()

  /** The number of elements in the priority queue. */
  private var _size: Int = 0

  /** Increases the capacity of the array.
    * @param minCapacity the desired minimum capacity
    */
  private def grow(minCapacity: Int): Unit = {
    val oldCapacity: Int = queue.length
    // Double size if small; else grow by 50%
    var newCapacity: Int = oldCapacity + (if (oldCapacity < 4096) oldCapacity + 2
    else oldCapacity >> 1)
    // overflow-conscious code
    if (newCapacity - HeapPriorityQueue.MAX_ARRAY_SIZE > 0) newCapacity = HeapPriorityQueue.hugeCapacity(minCapacity)
    queue = util.Arrays.copyOf(queue, newCapacity)
  }

  def pop: Node[S, T] = {
    val minNode: Node[S, T] = this.peek
    this.removeAt(0)
    minNode
  }

  def addOrUpdate(node: Node[S, T]): Boolean = {
    if (indexMap.contains(node)) {
      val index: Int = indexMap(node)
      this.siftUp(index, node)
      true
    }
    else {
      add(node)
      false
    }
  }

  /** Removes the ith element from queue.
    * First remove the last put where the removed one was.
    * Then shift it up.
    */
  private def removeAt(i: Int): Unit =  {
    assert(i >= 0 && i < size)
    _size -= 1
    val s: Int = _size
    val removed: Node[S, T] = queue(i)
    queue(i) = queue(s)
    queue(s) = null
    if (s != i) {// removing other than last
      val node: Node[S, T] = queue(i)
      siftDown(i, node)
    }
    indexMap.remove(removed)
  }

  def add(e: Node[S, T]): Boolean = offer(e)

  /** Inserts the specified element into this priority queue.
    * Throws NullPointerException if the specified element is null.
    * Throws ClassCastException if the specified element cannot be compared with elements currently
    *    in this priority queue according to the priority queue's ordering
    * @return true if the specified node is added
    */
  def offer(node: Node[S, T]): Boolean = {
    if (node == null) throw new NullPointerException
    val i: Int = _size
    if (i >= queue.length) grow(i + 1)
    _size = i + 1
    queue(i) = node
    indexMap.put(node, i)
    if (i != 0) siftUp(i, node)
    true
  }

  def peek: Node[S, T] = {
    if (size == 0) return null
    queue(0)
  }

  def size: Int = _size
  def isEmpty: Boolean = _size == 0

  /** Removes all of the elements from this priority queue. */
  def clear(): Unit =  {
    for (i <- 0 until size) {
      indexMap.remove(queue(i))
      queue(i) = null
    }
    _size = 0
  }

  /** Inserts item x at position k, maintaining heap invariant by
    * promoting x up the tree until it is greater than or equal to
    * its parent, or is the root.
    *
    * To simplify and speed up coercions and comparisons, the
    * Comparable and Comparator versions are separated into different
    * methods that are otherwise identical. (Similarly for siftDown.)
    *
    * @param k the position to fill
    * @param x the item to insert
    */
  private def siftUp(k: Int, x: Node[S, T]): Unit =  {
    if (comparator != null) siftUpUsingComparator(k, x)
    else siftUpComparable(k, x)
  }

  private def siftUpComparable(index: Int, key: Node[S, T]): Unit =  {
    var k = index
    var done = false
    while (k > 0 && !done) {
      val parent: Int = (k - 1) >>> 1
      val element: Node[S, T] = queue(parent)
      if (key.compareTo(element) < 0)  {
        queue(k) = element
        indexMap.put(element, k)
        k = parent
      } else done = true
    }
    queue(k) = key
    indexMap.put(key, k)
  }

  private def siftUpUsingComparator(index: Int, x: Node[S, T]): Unit =  {
    var k = index
    var done = false
    while (k > 0 && !done) {
      val parent: Int = (k - 1) >>> 1
      val e: Node[S, T] = queue(parent)
      if (comparator.compare(x, e) < 0) {
        queue(k) = e
        indexMap.put(e, k)
        k = parent
      } else done = true
    }
    queue(k) = x
    indexMap.put(x, k)
  }

  /** Inserts item x at position k, maintaining heap invariant by
    * demoting x down the tree repeatedly until it is less than or
    * equal to its children or is a leaf.
    *
    * @param k the position to fill
    * @param x the item to insert
    */
  private def siftDown(k: Int, x: Node[S, T]): Unit =  {
    if (comparator != null) siftDownUsingComparator(k, x)
    else siftDownComparable(k, x)
  }

  private def siftDownComparable(index: Int, key: Node[S, T]): Unit =  {
    var k = index
    var done = false
    val half: Int = _size >>> 1 // loop while a non-leaf
    while (k < half && !done) {
      var child: Int = (k << 1) + 1 // assume left child is least
      var c: Node[S, T] = queue(child)
      val right: Int = child + 1
      if (right < _size && c.compareTo(queue(right)) > 0)
        child = right
        c = queue(child)
      if (key.compareTo(c) > 0) {
        queue(k) = c
        indexMap.put(c, k)
        k = child
      } else done = true
    }
    queue(k) = key
    indexMap.put(key, k)
  }

  private def siftDownUsingComparator(index: Int, x: Node[S, T]): Unit =  {
    var k = index
    var done = false
    val half: Int = _size >>> 1
    while (k < half && !done) {
      var child: Int = (k << 1) + 1
      var c: Node[S, T] = queue(child)
      val right: Int = child + 1
      if (right < _size && comparator.compare(c, queue(right)) > 0)
        child = right
        c = queue(child)
      if (comparator.compare(x, c) > 0) {
        queue(k) = c
        indexMap.put(c, k)
        k = child
      } else done = true
    }
    queue(k) = x
    indexMap.put(x, k)
  }
}

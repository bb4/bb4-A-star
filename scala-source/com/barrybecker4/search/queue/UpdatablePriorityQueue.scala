package com.barrybecker4.search.queue

import com.barrybecker4.search.Node

/**
  * A priority queue that allows the priority keys to be updated dynamically.
  *
  * @author Barry Becker
  */
trait UpdatablePriorityQueue[S, T] {

  /** @return the node with the lowest priority */
  def pop: Node[S, T]

  /** Find the node with given state, and update its priority.
    * If the node is not currently in the heap, it is added.
    *
    * @param node node
    * @return true if the node was found and updated
    */
  def addOrUpdate(node: Node[S, T]): Boolean

  /** Inserts the specified element into this priority queue.
    * Throws NullPointerException if the specified element is null.
    * Throws ClassCastException if the specified element cannot be compared with elements currently
    * in this priority queue according to the priority queue's ordering
    *
    * @return { @code true} (as specified by { @link Collection#add})
    */
  def add(node: Node[S, T]): Boolean

  /** @return the number of elements in the queue */
  def size: Int

  /** @return true if no elements in the queue */
  def isEmpty: Boolean
}

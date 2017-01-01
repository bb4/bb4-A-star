/** Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.search

/**
  * @author Barry Becker
  */
trait UpdatablePriorityQueue[S, T] {

  /** @return the node with the lowest priority */
  def pop: Node[S, T]

  /**
    * Find the node with given state, and update its priority
    * If the node is not currently in the heap, it is added.
    *
    * @param node node
    * @return true if the node was found and updated
    */
  def addOrUpdate(node: Node[S, T]): Boolean

  /**
    * Inserts the specified element into this priority queue.
    * @return { @code true} (as specified by { @link Collection#add})
    * @throws ClassCastException if the specified element cannot be compared with elements currently
    *    in this priority queue according to the priority queue's ordering
    * @throws NullPointerException if the specified element is null
    */
  def add(node: Node[S, T]): Boolean

  /** @return the number of elements in the queue */
  def size: Int

  def isEmpty: Boolean
}

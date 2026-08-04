// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

/**
  * Shared bookkeeping for sequential search strategies (A*, IDA*, …).
  * Holds stop/solution/try-count state that [[solve]] resets between runs.
  */
private[search] abstract class AbstractSearcher[S, T] extends ISearcher[S, T] {

  /** Number of steps that it took to find solution */
  protected var numTries: Long = 0L

  /** Enables stopping the search via method call */
  private var stopped: Boolean = false

  protected var solution: Option[Node[S, T]] = None

  protected def isStopped: Boolean = stopped

  protected def resetSearchState(): Unit = {
    stopped = false
    solution = None
    numTries = 0L
  }

  def getPathToSolution: Option[Seq[T]] = solution.map(_.asTransitionList)

  /** Tell the search to stop */
  def stop(): Unit =
    stopped = true
}

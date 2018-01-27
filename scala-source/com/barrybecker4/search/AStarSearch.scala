// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import scala.collection.immutable.HashMap
import scala.collection.mutable

object AStarSearch {
  // A trivial example of how the search works
  def main(args:Array[String]) {
    val search = new AStarSearch[String, (String, String)](new TrivialSearchSpace())
    val path = search.solve.get
    println("path = " + path.mkString(", "))
  }
}

/**
  * Sequential search strategy that uses the A* search algorithm.
  * See http://en.wikipedia.org/wiki/A*_search_algorithm
  * S represents a state in the global search space.
  * T represents a transition from one state to the next.
  *
  * The performance of this search is very dependent on the design of the search space.
  * Here are some possible optimizations to consider when designing the SearchSpace and its components.
  * - The visited list may grow huge if the space is very large causing out of memory issues.
  * - Calculate distance metrics in the constructor (or using lazy initialization) of S. S and T should be immutable.
  * - Try to make the equals method in S as efficient as possible as it will be called a lot.
  * - When creating neighbors, use the fact that there is going to be an incremental change to the distance
  * and do not recompute it from scratch. Hint: use a private constructor, that takes the distance as a param.
  * - Sort the neighbors so that the most promising is delivered first.
  *
  * @param searchSpace the global search space containing initial and goal states.
  * @param openQueue   the specific updatable priority queue to use. Candidate nodes to search on the frontier.
  * @author Barry Becker
  */
class AStarSearch[S, T](val searchSpace: SearchSpace[S, T],
                        val openQueue: UpdatablePriorityQueue[S, T] = new HeapPriorityQueue[S, T])
  extends ISearcher[S, T] {

  /** Provides the cost for the lowest cost path from the specified start state to the goal state (g score) */
  private var pathCost: mutable.Map[S, Integer] = new mutable.HashMap[S, Integer]

  private var solution: Option[Node[S, T]] = None

  /** Number of steps that it took to find solution */
  private var numTries: Long = 0L

  /** States that have been visited, but they may be replaced if we can reach them by a better path */
  private[search] var visited: Map[S, Node[S, T]] = new HashMap[S, Node[S, T]]

  /** Enables stopping the search via method call */
  private var stopped: Boolean = false


  /**
    * @return a sequence of transitions leading from the initial state to the goal state. None if no path found.
    */
  def solve: Option[Seq[T]] = {
    val startTime: Long = System.currentTimeMillis
    initialize()
    val solutionState: Option[Node[S, T]] = search()
    val pathToSolution: Option[Seq[T]] = getSolution
    val solution: Option[S] = if (solutionState.isDefined) Some(solutionState.get.state) else None

    val elapsedTime: Long = System.currentTimeMillis - startTime
    searchSpace.finalRefresh(pathToSolution, solution, numTries, elapsedTime)
    pathToSolution
  }

  private def initialize() {
    stopped = false
    val startingState: S = searchSpace.initialState
    val startNode: Node[S, T] = new Node[S, T](startingState, searchSpace.distanceFromGoal(startingState))
    openQueue.add(startNode)
    pathCost.put(startingState, 0)
  }

  /** @return the solution - null until it is found */
  def getSolution: Option[Seq[T]] = if (solution.isDefined) Some(solution.get.asTransitionList) else None

  /** Tell the search to stop */
  def stop() { stopped = true }

  /**
    * Best first search for a solution.
    * @return the solution state node, if found, which has the path leading to a solution. Null if no solution.
    */
  protected def search(): Option[Node[S, T]] = {
    while (!openQueue.isEmpty && !stopped) {
      val currentNode: Option[Node[S, T]] = processNext(openQueue.pop)
      if (currentNode.isDefined) return currentNode
    }
    None // failed to find a solution
  }

  /**
    * Process the next node on the priority queue. Adds neighboring nodes to the queue.
    * @return the solution if it was found
    */
  private def processNext(currentNode: Node[S, T]): Option[Node[S, T]] = {
    val currentState: S = currentNode.state
    searchSpace.refresh(currentState, numTries)
    if (searchSpace.isGoal(currentState)) {
      // the extra check for a better path is needed when running concurrently
      if (solution.isEmpty || currentNode.pathCost < solution.get.pathCost)
        solution = Some(currentNode)
      return Some(currentNode) // success
    }
    visited += (currentState -> currentNode)
    val transitions: Seq[T] = searchSpace.legalTransitions(currentState)

    for (transition <- transitions) {
      val nbr: S = searchSpace.transition(currentState, transition)
      if (!visited.contains(nbr)) {
        val estPathCost: Int = pathCost(currentState) + searchSpace.getCost(transition)
        if (!pathCost.contains(nbr) || estPathCost < pathCost(nbr)) {
          val estFutureCost: Int = estPathCost + searchSpace.distanceFromGoal(nbr)
          val child: Node[S, T] =
            new Node[S, T](nbr, Some(transition), Some(currentNode), estPathCost, estFutureCost)
          pathCost.put(nbr, estPathCost)
          openQueue.addOrUpdate(child)
          numTries += 1
        }
      }
    }
    None
  }
}
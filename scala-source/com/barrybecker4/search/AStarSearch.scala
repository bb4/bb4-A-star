// Copyright by Barry G. Becker, 2017-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import com.barrybecker4.search.queue.{HeapPriorityQueue, UpdatablePriorityQueue}
import com.barrybecker4.search.space.{SearchSpace, TrivialSearchSpace}

import scala.collection.immutable.HashMap
import scala.collection.mutable

object AStarSearch {
  /** Small demo of A* on [[com.barrybecker4.search.space.TrivialSearchSpace]]
    * (also referenced as main class from the build). */
  def main(args: Array[String]): Unit = {
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
  * - Calculate distance metrics in the constructor (or using lazy initialization) of S. S and T must be immutable.
  * - Try to make the equals method in S as efficiently as possible as it will be called a lot.
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

  /** Provides the cost for the lowest cost path from the specified start state to some specified state (g score) */
  private val pathCost: mutable.Map[S, Int] = new mutable.HashMap[S, Int]

  private var solution: Option[Node[S, T]] = None

  /** Number of steps that it took to find solution */
  private var numTries: Long = 0L

  /** States that have been visited, but they may be replaced if we can reach them by a better path */
  private[search] var visited: Map[S, Node[S, T]] = HashMap.empty

  /** Enables stopping the search via method call */
  private var stopped: Boolean = false

  /**
    * @return a sequence of transitions leading from the initial state to the goal state. None if no path found.
    */
  def solve: Option[Seq[T]] = {
    val startTime: Long = System.currentTimeMillis
    resetSearchState()
    initialize()
    val solutionState: Option[Node[S, T]] = search()
    val pathToSolution: Option[Seq[T]] = getPathToSolution
    val solutionIfExists: Option[S] = solutionState.map(_.state)

    val elapsedTime: Long = System.currentTimeMillis - startTime
    searchSpace.finalRefresh(pathToSolution, solutionIfExists, numTries, elapsedTime)
    pathToSolution
  }

  /** Clears frontier and bookkeeping so [[solve]] can be called again on the same instance. */
  private def resetSearchState(): Unit = {
    stopped = false
    solution = None
    numTries = 0L
    pathCost.clear()
    visited = HashMap.empty
    openQueue.clear()
  }

  private def initialize(): Unit = {
    val startingState: S = searchSpace.initialState
    val startNode: Node[S, T] = new Node[S, T](startingState, searchSpace.distanceFromGoal(startingState))
    openQueue.add(startNode)
    pathCost.put(startingState, 0)
  }

  def getPathToSolution: Option[Seq[T]] = solution.map(_.asTransitionList)

  /** Tell the search to stop */
  def stop(): Unit =
    stopped = true

  /**
    * Best first search for a solution.
    * @return the solution state node, if found, which has the path leading to a solution. None if no solution.
    */
  protected def search(): Option[Node[S, T]] = {
    var found: Option[Node[S, T]] = None
    while (found.isEmpty && !openQueue.isEmpty && !stopped) {
      processNext(openQueue.pop).foreach { sol => found = Some(sol) }
    }
    found
  }

  /**
    * Process the next node on the priority queue. Adds neighboring nodes to the queue.
    * @return the solution node if it was found
    */
  private def processNext(currentNode: Node[S, T]): Option[Node[S, T]] = {
    val currentState: S = currentNode.state
    searchSpace.refresh(currentState, numTries)
    goalCheck(currentNode).orElse {
      visitAndExpand(currentState, currentNode)
      None
    }
  }

  private def goalCheck(currentNode: Node[S, T]): Option[Node[S, T]] = {
    val currentState = currentNode.state
    Option.when(searchSpace.isGoal(currentState)) {
      // the extra check for a better path is needed when running concurrently
      if (solution.forall(s => currentNode.pathCost < s.pathCost))
        solution = Some(currentNode)
      currentNode
    }
  }

  private def visitAndExpand(currentState: S, currentNode: Node[S, T]): Unit = {
    visited = visited.updated(currentState, currentNode)
    val transitions: Seq[T] = searchSpace.legalTransitions(currentState)
    for (transition <- transitions)
      considerNeighbor(currentState, currentNode, transition)
  }

  private def considerNeighbor(currentState: S, currentNode: Node[S, T], transition: T): Unit = {
    val nbr: S = searchSpace.transition(currentState, transition)
    if (visited.contains(nbr)) return

    val transitionCost = searchSpace.getCost(transition)
    val actPathCost: Int = pathCost(currentState) + transitionCost
    val prevBest = pathCost.get(nbr)
    if (prevBest.exists(_ <= actPathCost)) return

    val estTotalCost: Int = actPathCost + searchSpace.distanceFromGoal(nbr)
    val child: Node[S, T] =
      new Node[S, T](nbr, Some(transition), Some(currentNode), actPathCost, estTotalCost)
    pathCost.put(nbr, actPathCost)
    openQueue.addOrUpdate(child)
    numTries += 1
  }
}

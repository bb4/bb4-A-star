// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import com.barrybecker4.search.space.SearchSpace

import scala.util.boundary
import scala.util.boundary.break

/**
  * Sequential search strategy that uses the IDA* search algorithm.
  * See https://en.wikipedia.org/wiki/Iterative_deepening_A*
  * S represents a state in the global search space.
  * T represents a transition from one state to the next.
  *
  * The performance of this search is very dependent on the design of the search space.
  * Here are some possible optimizations to consider when designing the SearchSpace and its components.
  * - Calculate distance metrics in the constructor (or using lazy initialization) of S. S and T must be immutable.
  * - Try to make the equals method in S as efficiently as possible as it will be called a lot.
  * - When creating neighbors, use the fact that there is going to be an incremental change to the distance
  * and do not recompute it from scratch. Hint: use a private constructor, that takes the distance as a param.
  * - Sort the neighbors so that the most promising is delivered first.
  *
 * @param searchSpace          the global search space containing initial and goal states.
 * @param maxBoundIterations   max number of times the cost bound is raised before giving up;
 *                             default is large enough for typical puzzles (the previous fixed cap of 42 was too low).
 * @author Barry Becker
 */
class IDAStarSearch[S, T](val searchSpace: SearchSpace[S, T], val maxBoundIterations: Int = 1_000_000)
    extends ISearcher[S, T] {

  /** Number of steps that it took to find solution */
  private var numTries: Long = 0L

  /** Enables stopping the search via method call */
  private var stopped: Boolean = false

  private var solution: Option[Node[S, T]] = None

  /**
    * @return a sequence of transitions leading from the initial state to the goal state. None if no path found.
    */
  def solve: Option[Seq[T]] = {
    val startTime: Long = System.currentTimeMillis

    resetSearchState()

    val startingState: S = searchSpace.initialState
    val startNode: Node[S, T] = new Node[S, T](startingState, searchSpace.distanceFromGoal(startingState))

    solution = search(startNode)
    val pathToSolution: Option[Seq[T]] = getPathToSolution

    val elapsedTime: Long = System.currentTimeMillis - startTime

    val solutionIfExists: Option[S] = solution.map(_.state)
    searchSpace.finalRefresh(pathToSolution, solutionIfExists, numTries, elapsedTime)
    pathToSolution
  }

  private def resetSearchState(): Unit = {
    stopped = false
    solution = None
    numTries = 0L
  }

  def getPathToSolution: Option[Seq[T]] = solution.map(_.asTransitionList)

  /** Tell the search to stop */
  def stop(): Unit =
    stopped = true

  /**
    * Depth first search for a solution using iterative deepening. Explore the most promising nodes first.
    * Continue to expand an optimal patch from the startNode using iterative deepening of the search in the tree.
    * At each iteration, the threshold used for the next iteration is the minimum cost of all values
    * that exceeded the current threshold.
    * @return the solution state node, if found, which has the path leading to a solution. None if no solution.
    */
  protected def search(startNode: Node[S, T]): Option[Node[S, T]] = {
    var bound = startNode.estimatedTotalCost
    var currentNode = startNode
    var iteration = 0
    var done = false
    var result: Option[Node[S, T]] = None

    while !done && !stopped && result.isEmpty do
      val (newBound, newNode) = expandSearch(currentNode, bound)
      currentNode = newNode
      if newBound == 0 then
        result = Some(currentNode)
      else if newBound == Int.MaxValue || iteration > maxBoundIterations then
        done = true
      else
        iteration += 1
        bound = newBound
    end while

    result
  }

  /**
    * Recursively expand the search from the last frontier node.
    * Note that we never allow us to visit a node in the path again to avoid cycles.
    * @return (min, currentNode) where min is the new minimum bound,
    *         and currentNode is the new node on the path from the startNode.
    */
  private def expandSearch(node: Node[S, T], bound: Int): (Int, Node[S, T]) =
    boundary[(Int, Node[S, T])]:
      if stopped then break((Int.MaxValue, node))

      val estTotalCost = node.estimatedTotalCost
      if estTotalCost > bound then break((estTotalCost, node))

      val currentState: S = node.state
      if searchSpace.isGoal(currentState) then break((0, node))

      val transitions: Seq[T] = searchSpace.legalTransitions(currentState)
      searchSpace.refresh(currentState, numTries)
      val nbrNodes = buildSortedNeighborNodes(currentState, node, transitions)

      var min = Int.MaxValue
      var currentNode = node
      for nbrNode <- nbrNodes do
        if stopped then break((Int.MaxValue, currentNode))
        numTries += 1
        val (newBound, newNode) = expandSearch(nbrNode, bound)
        currentNode = newNode
        if newBound == 0 then break((0, currentNode))
        if newBound < min then min = newBound
        currentNode = currentNode.previous.get // backtrack
      end for

      assert(currentNode == node)
      (min, node)

  private def buildSortedNeighborNodes(currentState: S, currentNode: Node[S, T], transitions: Seq[T]): Seq[Node[S, T]] = {
    val builders = transitions.flatMap { trans =>
      val nbr: S = searchSpace.transition(currentState, trans)
      Option.when(!currentNode.containsStateInPath(nbr)) {
        val transitionCost = searchSpace.getCost(trans)
        val pathCost = currentNode.pathCost + transitionCost
        val estRemainingCost: Int = searchSpace.distanceFromGoal(nbr)
        new Node[S, T](nbr, Some(trans), Some(currentNode), pathCost, pathCost + estRemainingCost)
      }
    }
    builders.sorted
  }
}

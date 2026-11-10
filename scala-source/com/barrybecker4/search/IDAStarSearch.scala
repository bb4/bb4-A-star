// Copyright by Barry G. Becker, 2021-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import com.barrybecker4.search.space.{SearchSpace, TrivialSearchSpace}

import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

object IDAStarSearch {
  /** Small demo of IDA* on [[com.barrybecker4.search.space.TrivialSearchSpace]] */
  def main(args: Array[String]): Unit = {
    val search = new IDAStarSearch[String, (String, String)](new TrivialSearchSpace())
    val path = search.solve.get
    println("path = " + path.mkString(", "))
  }
}

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
  * Caps (`maxNodeExpansions`, `maxBoundIterations`, [[stop]]) cause [[solve]] to return `None`. That does not
  * distinguish unsolvable from exhausted/stopped; callers can use
  * [[com.barrybecker4.search.space.SearchSpace.isKnownUnsolvable]], external
  * parity checks, or inspect [[nodeExpansions]].
  *
  * @param searchSpace the global search space containing initial and goal states.
  * @param config      limits, weighting, and optional pruning; see [[IDAStarSearchConfig]]
  * @author Barry Becker
  */
class IDAStarSearch[S, T](val searchSpace: SearchSpace[S, T],
                          val config: IDAStarSearchConfig = IDAStarSearchConfig())
    extends AbstractSearcher[S, T] {

  /** Preserves `new IDAStarSearch(space, maxBoundIterations)` call sites. */
  def this(searchSpace: SearchSpace[S, T], maxBoundIterations: Int) =
    this(searchSpace, IDAStarSearchConfig(maxBoundIterations = maxBoundIterations))

  /** Neighbor expansions counted during the last [[solve]] (`numTries`). */
  def nodeExpansions: Long = numTries

  private def limitReached: Boolean =
    isStopped || config.maxNodeExpansions.exists(_ <= numTries)

  /**
    * @return a sequence of transitions leading from the initial state to the goal state. None if no path found.
    */
  def solve: Option[Seq[T]] = {
    val startTime: Long = System.currentTimeMillis

    resetSearchState()

    if searchSpace.isKnownUnsolvable then
      val elapsed: Long = System.currentTimeMillis - startTime
      searchSpace.finalRefresh(None, None, numTries, elapsed)
      return None

    val startingState: S = searchSpace.initialState
    val h0 = searchSpace.distanceFromGoal(startingState)
    val startNode: Node[S, T] = new Node[S, T](startingState, weightedTotalCost(0, h0))

    solution = search(startNode)
    val pathToSolution: Option[Seq[T]] = getPathToSolution

    val elapsedTime: Long = System.currentTimeMillis - startTime

    val solutionIfExists: Option[S] = solution.map(_.state)
    searchSpace.finalRefresh(pathToSolution, solutionIfExists, numTries, elapsedTime)
    pathToSolution
  }

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

    while !done && !limitReached && result.isEmpty do
      val seen: Option[mutable.Set[S]] =
        if config.useIterationTransposition then Some(mutable.Set.empty[S]) else None
      val (newBound, newNode) = expandSearch(currentNode, bound, seen)
      currentNode = newNode
      if newBound == 0 then
        result = Some(currentNode)
      else if newBound == Int.MaxValue || iteration > config.maxBoundIterations then
        done = true
      else if config.terminateWhenBoundUnchanged && newBound == bound then
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
  private def expandSearch(node: Node[S, T], bound: Int,
                           seen: Option[mutable.Set[S]]): (Int, Node[S, T]) =
    boundary[(Int, Node[S, T])]:
      if limitReached then break((Int.MaxValue, node))

      val estTotalCost = node.estimatedTotalCost
      if estTotalCost > bound then break((estTotalCost, node))

      val currentState: S = node.state
      if searchSpace.isGoal(currentState) then break((0, node))

      seen.foreach { s =>
        if searchSpace.alreadySeen(currentState, s) then break((Int.MaxValue, node))
      }

      val transitions: Seq[T] = searchSpace.legalTransitions(currentState)
      searchSpace.refresh(currentState, numTries)
      val nbrNodes = buildSortedNeighborNodes(currentState, node, transitions)

      var min = Int.MaxValue
      var currentNode = node
      for nbrNode <- nbrNodes do
        if limitReached then break((Int.MaxValue, currentNode))
        numTries += 1
        val (newBound, newNode) = expandSearch(nbrNode, bound, seen)
        currentNode = newNode
        if newBound == 0 then break((0, currentNode))
        if newBound < min then min = newBound
        currentNode = currentNode.previous.get // backtrack
      end for

      assert(currentNode == node)
      (min, node)

  private def buildSortedNeighborNodes(currentState: S, currentNode: Node[S, T],
                                       transitions: Seq[T]): Seq[Node[S, T]] = {
    val builders = transitions.flatMap { trans =>
      val nbr: S = searchSpace.transition(currentState, trans)
      Option.when(!currentNode.containsStateInPath(nbr)) {
        val transitionCost = searchSpace.getCost(trans)
        val pathCost = currentNode.pathCost + transitionCost
        val estRemainingCost: Int = searchSpace.distanceFromGoal(nbr)
        new Node[S, T](nbr, Some(trans), Some(currentNode), pathCost,
          weightedTotalCost(pathCost, estRemainingCost))
      }
    }
    builders.sorted
  }

  /**
    * Weighted f-cost. At `weightPercent == 100` this is `pathCost + h` (unchanged from classic IDA*).
    * Otherwise uses `pathCost * 100 + h * weightPercent` (bb4-puzzles slider semantics).
    */
  private def weightedTotalCost(pathCost: Int, h: Int): Int =
    if config.weightPercent == 100 then pathCost + h
    else pathCost * 100 + h * config.weightPercent
}

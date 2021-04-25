// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

import com.barrybecker4.search.space.SearchSpace


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
  * @param searchSpace the global search space containing initial and goal states.
  * @author Barry Becker
  */
class IDAStarSearch[S, T](val searchSpace: SearchSpace[S, T]) extends ISearcher[S, T] {

  /** Number of steps that it took to find solution */
  private var numTries: Long = 0L

  /** Enables stopping the search via method call */
  private var stopped: Boolean = false

  private var solution: Option[Node[S, T]] = None

  /** If the depth is greater than this, we will probably never find a solution */
  private val MAX_DEPTH = 42


  /**
    * @return a sequence of transitions leading from the initial state to the goal state. None if no path found.
    */
  def solve: Option[Seq[T]] = {
    val startTime: Long = System.currentTimeMillis

    stopped = false
    val startingState: S = searchSpace.initialState
    val startNode: Node[S, T] = new Node[S, T](startingState, searchSpace.distanceFromGoal(startingState))

    solution = search(startNode)
    val pathToSolution: Option[Seq[T]] = getPathToSolution

    val elapsedTime: Long = System.currentTimeMillis - startTime

    val solutionIfExists: Option[S] = if (solution.isDefined) Some(solution.get.state) else None
    searchSpace.finalRefresh(pathToSolution, solutionIfExists, numTries, elapsedTime)
    pathToSolution
  }

  def getPathToSolution: Option[Seq[T]] =
    if (solution.isDefined) Some(solution.get.asTransitionList) else None

  /** Tell the search to stop */
  def stop(): Unit =
    stopped = true

  /**
    * Depth first search for a solution using iterative deepening. Explore the most promising nodes first.
    * Continue to expand an optimal patch from the startNode using iterative deepening of the search in the tree.
    * At each iteration, the threshold used for the next iteration is the minimum cost of all values
    * that exceeded the current threshold.
    * @return the solution state node, if found, which has the path leading to a solution. Null if no solution.
    */
  protected def search(startNode: Node[S, T]): Option[Node[S, T]] = {
    var bound = startNode.estimatedTotalCost
    var currentNode = startNode
    var done = false
    var ct = 0

    while (!done) {
      val (newBound, newNode) = expandSearch(currentNode, bound)
      //println("Expanded bound: " + newBound + "  cost: " + newNode.pathCost + "  depth: " + depth(newNode) + "  ct: " + ct)
      currentNode = newNode
      if (newBound == 0)
        return Some(currentNode)
      if (newBound == Int.MaxValue || ct > MAX_DEPTH)
        done = true
      ct += 1
      bound = newBound
    }
    None
  }

  /**
    * Recursively expand the search from the last frontier node.
    * Node that we never allow us to visit a node in the path again to avoid cycles.
    * @return (min, currentNode) where min is the new minimum bound,
    *         and currentNode is the new node on the path from the startNode.
    */
  private def expandSearch(node: Node[S, T], bound: Int): (Int, Node[S, T])= {
    var currentNode = node

    val currentState: S = currentNode.state
    val estTotalCost = currentNode.estimatedTotalCost

    if (estTotalCost > bound)
      return (estTotalCost, currentNode)
    if (searchSpace.isGoal(currentState)) {
      return (0, currentNode) // success
    }
    var min = Int.MaxValue
    var nbrNodes: Seq[Node[S, T]] = Seq()
    val transitions: Seq[T] = searchSpace.legalTransitions(currentState)
    searchSpace.refresh(currentState, numTries)

    transitions.foreach(trans => {
      val nbr: S = searchSpace.transition(currentState, trans)
      if (!currentNode.containsStateInPath(nbr)) {
        val transitionCost = searchSpace.getCost(trans)
        val pathCost = currentNode.pathCost + transitionCost
        val estRemainingCost: Int = searchSpace.distanceFromGoal(nbr)
        val node = new Node[S, T](nbr, Some(trans), Some(currentNode), pathCost, pathCost + estRemainingCost)
        nbrNodes :+= node
      }
    })

    for (nbrNode <- nbrNodes.sorted) {
      numTries += 1
      val (newBound, newNode) = expandSearch(nbrNode, bound)
      currentNode = newNode
      if (newBound == 0)
        return (0, currentNode)
      if (newBound < min) {
        min = newBound
      }
      currentNode = currentNode.previous.get // backtrack
    }

    assert (currentNode == node)
    (min, node)
  }


  private def depth(node: Node[S, T]): Int = {
    var depth = 0
    var n = node;
    while (n.previous.isDefined) {
      depth += 1
      n = n.previous.get
    }
    depth
  }

}

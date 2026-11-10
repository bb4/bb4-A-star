// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search

/**
  * Configuration for [[IDAStarSearch]].
  *
  * @param maxBoundIterations         max times the cost bound may be raised before giving up
  * @param maxNodeExpansions          optional cap on neighbor expansions (`numTries`); when hit, [[IDAStarSearch.solve]]
  *                                   returns `None` (same as stop / exhausted search)
  * @param weightPercent              heuristic weight in percent. `100` is standard admissible IDA* (optimal when `h` is
  *                                   admissible). Values above `100` make search greedier: faster, but may return a
  *                                   longer-than-optimal path (same semantics as bb4-puzzles `FastSliderSolver`)
  * @param useIterationTransposition  when true, each bound iteration tracks visited states via
  *                                   [[com.barrybecker4.search.space.SearchSpace.alreadySeen]] (memory tradeoff for
  *                                   pruning duplicate states within one iteration)
  * @param terminateWhenBoundUnchanged when true, stop if a completed iteration yields the same next bound as the
  *                                   current bound (helps some unsolvable spaces; off by default)
  */
final case class IDAStarSearchConfig(
  maxBoundIterations: Int = 1_000_000,
  maxNodeExpansions: Option[Long] = None,
  weightPercent: Int = 100,
  useIterationTransposition: Boolean = false,
  terminateWhenBoundUnchanged: Boolean = false,
)

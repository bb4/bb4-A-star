# A-star and IDA-star

📊 [Build status for all bb4 projects](https://github.com/bb4/.github)

Scala implementation of generic [A*](https://en.wikipedia.org/wiki/A*_search_algorithm) and [IDA*](https://en.wikipedia.org/wiki/Iterative_deepening_A*) search algorithms.

## When to use A* vs IDA*

| | **A\*** (`AStarSearch`) | **IDA\*** (`IDAStarSearch`) |
|---|---|---|
| Memory | Open set + visited map; can exhaust heap on huge spaces | Path-only (plus optional per-iteration transposition) |
| Speed | Usually faster when memory fits | Trades time for memory; re-expands nodes across bounds |
| Best for | Moderate spaces (e.g. small sliding puzzles) | Huge spaces (Rubik-scale) where A* runs out of memory |

Both share `AbstractSearcher` bookkeeping (`stop()`, `numTries` / expansions, `finalRefresh`).

## IDA* configuration

```scala
val search = new IDAStarSearch(space) // defaults
val search = new IDAStarSearch(space, maxBoundIterations) // legacy overload
val search = new IDAStarSearch(space, IDAStarSearchConfig(
  maxBoundIterations = 1_000_000,
  maxNodeExpansions = Some(5_000_000L), // optional UX / timeout cap
  weightPercent = 100,                  // 100 = optimal when h is admissible
  useIterationTransposition = false,    // optional seen-set per bound (memory tradeoff)
  terminateWhenBoundUnchanged = false,
))
```

- **Limits / `stop()`:** Hitting `maxNodeExpansions`, `maxBoundIterations`, or `stop()` makes `solve` return `None` and still calls `searchSpace.finalRefresh`. Caps do not distinguish “unsolvable” from “gave up”.
- **Weighted IDA\*:** `weightPercent > 100` scales the heuristic like bb4-puzzles’ slider `FastSliderSolver` (`f = g*100 + h*weightPercent`). Faster, but may return a longer-than-optimal path.
- **Unsolvability:** Override `SearchSpace.isKnownUnsolvable` (e.g. sliding-puzzle parity) for an immediate `None`. Otherwise use expansion caps for runaway spaces.

## Integration

Puzzle UIs typically wrap this library via bb4-puzzles `IDAStarPuzzleSolver` (extends `IDAStarSearch`). A specialized low-memory weighted slider solver (`FastSliderSolver`) remains in bb4-puzzles; it may later thin out and delegate weighted IDA* here.

## Build Instructions

* Install git, scala
* git clone https://github.com/barrybecker4/bb4-A-star.git
* In bash shell run `./gradlew`
* Or import Gradle project into IntelliJ and run from there (preferred)

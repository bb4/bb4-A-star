# A-star and IDA-star
Scala implementation of generic [A-Star](https://en.wikipedia.org/wiki/A*_search_algorithm) [IDA-star](https://en.wikipedia.org/wiki/Iterative_deepening_A*) search algorithms 

A-Star search uses a mutable heap priority queue. It is a fast search, but can be very space intensive if the search space is large.

IDA-Star is a version that uses depth first search iterative deepening in order to trade some speed for reduced memory. Some problem spaces, like Rubix cube for example, have such huge problem spaces that regular A-star can quickly exhaust memory.


## Build Instructions

* Install git, scala
* git clone https://github.com/barrybecker4/bb4-A-star.git
* In bash shell run `./gradlew`
* Or import gradle project into intellij and run from there (preferred)
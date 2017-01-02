package com.barrybecker4.search.slidingpuzzle

import java.util.Arrays

import com.barrybecker4.search.Location


/**
  * Immutable.
  *
  * Here is a list of performance enhancements that I made and how important they are:
  * - calculated the chang in the manhattan distance in the constructor, and cache it in a private property.
  * - use lazy calculation of the hamming distance. IOW, calculate the first time requested and cache it.
  * - made blocks use byte instead of int. short is probably best.
  * - use a 1D array instead of a 2D array for the blocks.
  * - Calculate the stringForm (from toString) in the constructor and cache it.
  * This should not be needed, and is bad because it adds memory. But I needed it in order to use the stringForm
  * as a hashcode for use in a HashMap (that also should not be needed, but I could not get reasonable times without)
  * - Use hamming distance as a sort of hashcode in the equals method to speed it up, since the normal way of computing
  * equals is slow. IOW first check the hamming value, if that does not match, it cannot be equal. If it does match
  * resort to slower comparison.
  * - When creating neighbors I use the fact that there is going to be an incremental change to the manhattan distance
  * and do not recompute it from scratch. Hint: use a private constructor, that takes the manhattan distance as a param.
  * - Sorted the neighbors so that the most promising is delivered first. Gave a modest performance boost because
  * fewer nodes were then added to the queue in the long run.
  * - Used System.arraycopy(src, 0, target, 0,length); to copy the internal blocks array.
  *
  * @author Barry Becker
  */
object Board {
  private def makeBlocks(src: Array[Array[Int]]): Array[Byte] = {
    val length: Int = src.length
    val target: Array[Byte] = new Array[Byte](length * length)
    for (i <- 0 until length) {
      for (j <- 0 until length) {
        target(i * length + j) = src(i)(j).toByte
      }
    }
    target
  }

  private def copyBlocks(src: Array[Byte]): Array[Byte] = {
    val length: Int = src.length
    val target: Array[Byte] = new Array[Byte](length)
    System.arraycopy(src, 0, target, 0, length)
    target
  }
}

class Board(blocks: Array[Byte], manhattanDist: Int = -1) {

  private val _blocks: Array[Byte] = blocks
  private val side: Byte = Math.sqrt(_blocks.length).toByte
  private var _hamming: Byte = -1
  var _manhattan: Int = if (manhattanDist == -1) calculateManhattan else manhattanDist
  private var _hashCode: Int = -1

  /**
    * Construct a board from an N-by-N array of blocks
    * @param blocks 0 - N*N blocks. blocks[i][j] = block in row i, column j
    */
  def this(blocks: Array[Array[Int]]) {
    this(Board.makeBlocks(blocks))
  }

  /** @return board dimension N */
  def dimension: Int = side

  /** @return number of blocks out of place */
  def hamming: Int = {
    if (_hamming < 0) _hamming = calculateHamming
    _hamming
  }

  private def calculateHamming: Byte = {
    var expected: Byte = 0
    var hamCount: Byte = 0
    for (i <- 0 until side) {
      for (j <- 0 until side) {
            val value: Byte = _blocks(i * side + j)
            expected = (expected + 1).toByte
            if (value != 0 && value != expected) hamCount = (hamCount + 1).toByte
      }
    }
    hamCount
  }

  /** @return sum of Manhattan distances between blocks and goal */
  def manhattan: Int = _manhattan

  private def calculateManhattan: Int = {
    var totalDistance: Int = 0
    for (i <- 0 until side) {
      for (j <- 0 until side) {
        val value: Int = _blocks(i * side + j)
        if (value != 0) {
          val expCol: Int = (value - 1) % side
          val expRow: Int = (value - 1) / side
          val deltaRow: Int = Math.abs(expRow - i)
          val deltaCol: Int = Math.abs(expCol - j)
          totalDistance += deltaRow + deltaCol
        }
      }
    }
    totalDistance
  }

  override def equals(other: Any): Boolean = {
    other match {
      case other: Board => hamming == other.hamming && Arrays.equals(_blocks, other._blocks)
      case _ => false
    }
  }

  override def hashCode: Int = {
    if (_hashCode < 0) _hashCode = Arrays.hashCode(_blocks)
    _hashCode
  }

  /** @return true if this board the goal board */
  def isGoal: Boolean = hamming == 0

  /** @return a board that is obtained by exchanging two adjacent blocks in the same row */
  def twin: Board = {
    val newBlocks: Array[Byte] = Board.copyBlocks(this._blocks)
    if (newBlocks(0) != 0 && newBlocks(1) != 0) swap(0, 0, 0, 1, newBlocks)
    else swap(1, 0, 1, 1, newBlocks)
    new Board(newBlocks)
  }

  def getNeighborTransitions: List[Transition] = {
    var neighbors: List[Transition] = Nil
    val spacePos: Location = getSpacePosition
    val i: Int = spacePos.row
    val j: Int = spacePos.col
    if (i > 0) neighbors :+= new Transition(spacePos, new Location(i - 1, j))
    if (i < side - 1) neighbors :+= new Transition(spacePos, new Location(i + 1, j))
    if (j > 0) neighbors :+= new Transition(spacePos, new Location(i, j - 1))
    if (j < side - 1) neighbors :+= new Transition(spacePos, new Location(i, j + 1))
    neighbors
  }

  def applyTransition(trans: Transition): Board = {
    val space: Location = trans.spacePosition
    val tile: Location = trans.tilePosition
    move(space.row, space.col, tile.row, tile.col)
  }

  /**
    * @return all neighboring boards. There are at most 4.
    */
  def neighbors: Iterable[Board] = {
    var neighbors: List[Board] = Nil
    val spacePos: Location = getSpacePosition
    val i: Int = spacePos.row
    val j: Int = spacePos.col
    if (i > 0) neighbors  :+= move(i, j, i - 1, j)
    if (i < side - 1) neighbors :+= move(i, j, i + 1, j)
    if (j > 0) neighbors :+= move(i, j, i, j - 1)
    if (j < side - 1) neighbors :+= move(i, j, i, j + 1)

    neighbors = neighbors.sortBy(_._manhattan)
    neighbors
  }

  private def move(oldSpaceRow: Int, oldSpaceCol: Int, newSpaceRow: Int, newSpaceCol: Int): Board = {
    val newBlocks: Array[Byte] = Board.copyBlocks(_blocks)
    val movingVal: Int = _blocks(newSpaceRow * side + newSpaceCol)
    val goalCol: Int = (movingVal - 1) % side
    val goalRow: Int = (movingVal - 1) / side
    val oldDist: Int = Math.abs(newSpaceRow - goalRow) + Math.abs(newSpaceCol - goalCol)
    val newDist: Int = Math.abs(oldSpaceRow - goalRow) + Math.abs(oldSpaceCol - goalCol)
    val distImprovement: Int = oldDist - newDist
    swap(oldSpaceRow, oldSpaceCol, newSpaceRow, newSpaceCol, newBlocks)
    new Board(newBlocks, _manhattan - distImprovement)
  }

  /**
    * @return row column coordinates of the space position
    */
  private def getSpacePosition: Location = {
    var i: Byte = 0
    for (i <- 0 until side) {
      for (j <- 0 until side) {
        if (_blocks(i * side + j) == 0) return new Location(i, j)
      }
    }
    throw new IllegalStateException("No space position!")
  }

  private def swap(row1: Int, col1: Int, row2: Int, col2: Int, b: Array[Byte]) {
    val p1: Byte = (row1 * side + col1).toByte
    val p2: Byte = (row2 * side + col2).toByte
    val temp: Byte = b(p1)
    b(p1) = b(p2)
    b(p2) = temp
  }

  /** @return string representation of this board  */
  override def toString: String = getStringForm

  private def getStringForm: String = {
    val str: StringBuilder = new StringBuilder
    str.append(side).append("\n")
    for (i <- 0 until side) {
      for (j <- 0 until side) {
        val value = _blocks(i * side + j)
        str.append(f"$value%2d ")
      }
      str.append("\n")
    }
    str.toString
  }
}
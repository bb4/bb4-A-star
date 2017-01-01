package com.barrybecker4.common

object Location {
  def apply(row: Int, col: Int) = new Location(row, col)
}

class Location(val row: Int, val col: Int) {
  override def toString: String = "(" + row + ", " + col + ")"

  /**
    * Checks to see if the given location has the same coordinates as this one.
    * @param location The location whose coordinates are to be compared.
    * @return true  The location's coordinates exactly equal this location's.
    */
  override def equals(location: Any): Boolean = {
    location match {
      case loc: Location => (loc.row == row) && (loc.col == col)
      case _ => false
    }
  }

  /** If override equals, should also override hashCode */
  override def hashCode: Int = 1023 * row + col
}

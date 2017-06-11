// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.search.slidingpuzzle

import com.barrybecker4.search.Location


/**
  * Move from one board state to another by shifting 1 tile to the space position. Immutable.
  *
  * @author Barry Becker
  */
class Transition(val spacePosition: Location, val tilePosition: Location) {

  override def toString: String = "from " + tilePosition + " to " + spacePosition
}

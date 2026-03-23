// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.search.space

import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class AbstractSearchSpaceSuite extends AnyFunSuite {

  test("alreadySeen marks state on first visit only") {
    val seen = mutable.Set[String]()
    val space = new AbstractSearchSpace[String, String]("x") {
      override def isGoal(state: String): Boolean = false

      override def legalTransitions(state: String): Seq[String] = Seq.empty

      override def transition(state: String, transition: String): String = state

      override def distanceFromGoal(state: String): Int = 0
    }

    assert(!space.alreadySeen("here", seen))
    assert(space.alreadySeen("here", seen))
    assert(seen.contains("here"))
  }
}

/*
 * Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.search.slidingpuzzle

import java.io.InputStream
import java.util.Scanner


/**
  * Read a puzzle board of any size
  *
  * @author Barry Becker
  */
class BoardReader(val prefix: String = "") {
  def read(filename: String): Board = {
    val str: InputStream = getClass.getResourceAsStream(prefix + filename)
    assert(str != null)
    val in: Scanner = new Scanner(str)
    val sidLen: Int = in.nextInt
    in.nextLine()
    val lines = for (i <- 0 until sidLen) yield in.nextLine()
    val blocks: Array[Array[Int]] = lines.map(_.trim().split("\\s+").map(_.toInt)).toArray
    //println("BLOCKS = " + blocks.map(_.mkString(", ")).mkString("\n"))

    new Board(blocks)
  }
}

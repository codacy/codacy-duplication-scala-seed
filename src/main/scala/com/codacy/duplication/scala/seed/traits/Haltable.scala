package com.codacy.duplication.scala.seed.traits

trait Haltable {

  def halt(status: Int): Unit = {
    Runtime.getRuntime.halt(status)
  }

}

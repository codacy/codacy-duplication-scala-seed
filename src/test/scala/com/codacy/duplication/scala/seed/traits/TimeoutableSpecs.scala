package com.codacy.duplication.scala.seed.traits

import org.specs2.matcher.ResultMatchers.beSuccessful
import org.specs2.mutable.Specification

import scala.concurrent.duration._
import scala.concurrent.{Await, TimeoutException}

class TimeoutableSpecs extends Specification with Timeoutable {

  "Timeout" should {
    "should throw exception" in {
      val f = onTimeout(100.seconds) {
        failure("Await.result didn't throw a scala.concurrent.TimeoutException.")
      }

      Await.result(f, 1.second) must throwA[TimeoutException]
    }

    "shouldn't throw exception" in {
      val f = onTimeout(1.seconds) {
        success("Await.result didn't throw a scala.concurrent.TimeoutException")
      }

      Await.result(f, Duration.Inf) must beSuccessful
    }
  }
}

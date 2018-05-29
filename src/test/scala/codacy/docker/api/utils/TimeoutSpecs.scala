package codacy.docker.api.utils

import org.specs2.mutable.Specification

import scala.concurrent.duration._
import scala.concurrent.{Await, TimeoutException}

class TimeoutSpecs extends Specification with Timeout {

  "Timeout" should {
    "should throw exception" in {
      val f = initTimeout(100.seconds) {
        failure("It should fail because a timeoutException wasn't thrown.")
      }

      Await.result(f, 1.second) must throwA[TimeoutException]
    }

    "shouldn't throw exception" in {
      val f = initTimeout(1.seconds) {
        success("The delay didn't throw a TimeoutException exception")
      }

      Await.result(f, Duration.Inf) must not(throwA[TimeoutException])
    }
  }
}

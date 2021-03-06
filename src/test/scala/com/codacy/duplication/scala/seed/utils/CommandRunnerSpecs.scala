package com.codacy.duplication.scala.seed.utils

import org.specs2.mutable.Specification

class CommandRunnerSpecs extends Specification {

  val genericCMD = List("echo", "foo")
  val invalidCMD = List("rm", "nofile.ext")
  val errorCMD = List("rmzzz", "nofile.ext")

  "CommandRunner" should {
    "simpleEchoExec" in {
      val result: Either[Throwable, CommandResult] = CommandRunner.exec(genericCMD)

      result.map(_.stdout) must beRight(===(List("foo")))
      result.map(_.exitCode) must beRight(0)
    }

    "handleInvalidExec" in {
      val result: Either[Throwable, CommandResult] = CommandRunner.exec(invalidCMD)

      result.map(_.stdout) must beRight(===(List.empty[String]))
      result.map(_.exitCode) must beRight(1)
    }

    "handleErrorExec" in {
      val result: Either[Throwable, CommandResult] = CommandRunner.exec(errorCMD)

      result must beLeft
    }
  }
}

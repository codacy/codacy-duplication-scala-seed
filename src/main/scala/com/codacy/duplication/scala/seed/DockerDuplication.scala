package com.codacy.duplication.scala.seed

import com.codacy.duplication.scala.seed.traits.{Delayed, Haltable}
import com.codacy.plugins.api.Source.Directory
import com.codacy.plugins.api.docker.v2.DuplicationResult
import com.codacy.plugins.api.duplication.DuplicationTool

import scala.util.{Failure, Success, Try}

class DockerDuplication(tool: DuplicationTool[DuplicationResult],
                        environment: DockerDuplicationEnvironment = new DockerDuplicationEnvironment)(
  printer: ResultsPrinter = new ResultsPrinter(isDebug = environment.isDebug))
    extends Delayed
    with Haltable {

  def main(args: Array[String]): Unit = {
    onTimeout(environment.timeout) {
      printer.log(s"timed out after ${environment.timeout} ")
      halt(2)
    }

    (for {
      config <- environment.config()
      results <- withNativeTry[List[DuplicationResult]](
        tool.apply(path = Directory(environment.sourcePath.toString),
                   language = config.language,
                   options = config.params.getOrElse(Map.empty)))
    } yield results) match {
      case Success(clones: Seq[DuplicationResult]) =>
        printer.printResults(clones, environment.sourcePath.toString)
        halt(0)

      case Failure(error) =>
        printer.logStackTrace(error)
        halt(1)
    }

  }

  @SuppressWarnings(Array("CatchThrowable"))
  private def withNativeTry[T](block: => Try[T]): Try[T] = {
    try {
      block
    } catch {
      case t: Throwable =>
        Failure[T](t)
    }
  }

}

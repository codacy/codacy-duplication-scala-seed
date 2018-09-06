package com.codacy.duplication.scala.seed

import java.nio.file.Path

import com.codacy.duplication.scala.seed.traits.{Haltable, Timeoutable}
import com.codacy.plugins.api.Source.Directory
import com.codacy.plugins.api.duplication._

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}

abstract class DockerDuplication(tool: DuplicationTool,
                                 environment: DockerDuplicationEnvironment = new DockerDuplicationEnvironment)(
  sourcePath: Path = environment.defaultSourcePath,
  configFile: Path = environment.defaultConfigFilePath,
  timeout: FiniteDuration = environment.timeout,
  printer: ResultsPrinter = new ResultsPrinter(isDebug = environment.isDebug))
    extends Timeoutable
    with Haltable {

  def main(args: Array[String]): Unit = {
    onTimeout(timeout) {
      printer.log(s"Timed out after $timeout")
      halt(2)
    }

    (for {
      config <- environment.configuration(configPath = configFile)
      results <- withNativeTry[Seq[DuplicationClone]](
        tool.apply(path = Directory(sourcePath.toString),
                   language = config.language,
                   options = config.params.getOrElse(Map.empty)))
    } yield results) match {
      case Success(clones: Seq[DuplicationClone]) =>
        printer.printResults(clones, sourcePath.toString)
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

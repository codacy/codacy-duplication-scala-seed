package codacy.dockerApi

import akka.actor.ActorSystem
import codacy.dockerApi.DockerEnvironment._
import codacy.dockerApi.traits.IDuplicationImpl
import play.api.libs.json.{Json, Writes}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

abstract class DockerDuplication(runner: IDuplicationImpl) {

  lazy val sys = ActorSystem("timeoutSystem")

  def initTimeout(duration: FiniteDuration) = {
    implicit val ct: ExecutionContext = sys.dispatcher
    sys.scheduler.scheduleOnce(duration) {
      Runtime.getRuntime.halt(2)
    }
  }

  lazy val timeout = Option(System.getProperty("timeout")).flatMap { case rawDuration =>
    Try(Duration(rawDuration)).toOption.collect { case d: FiniteDuration => d }
  }.getOrElse(30.minutes)

  lazy val isDebug = Option(System.getProperty("debug")).flatMap { case rawDebug =>
    Try(rawDebug.toBoolean).toOption
  }.getOrElse(false)

  def log(message: String): Unit = if (isDebug) {
    Console.err.println(s"[DockerDuplication] $message")
  }

  def main(args: Array[String]): Unit = {
    initTimeout(timeout)

    config.flatMap(config =>
      runner(
        path = sourcePath,
        config = config.duplication
      )
    ) match {
      case Success(clones) =>
        clones.foreach { clone =>
          val relativizedClone = clone.copy(files = clone.files.map {
            file =>
              file.copy(filePath = relativize(file.filePath))
          })
          logResult(relativizedClone)
        }
        Runtime.getRuntime.halt(0)

      case Failure(error) =>
        error.printStackTrace(Console.err)
        Runtime.getRuntime.halt(1)
    }
  }

  private def relativize(path: String) = {
    path.stripPrefix(DockerEnvironment.sourcePath.toString).stripPrefix("/")
  }

  private def logResult[T](result: T)(implicit fmt: Writes[T]) = {
    println(Json.stringify(Json.toJson(result)))
  }

}

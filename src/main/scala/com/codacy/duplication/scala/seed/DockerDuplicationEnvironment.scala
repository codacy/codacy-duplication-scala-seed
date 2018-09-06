package com.codacy.duplication.scala.seed

import java.nio.file.{Files, Path, Paths}

import com.codacy.plugins.api.duplication.DuplicationTool._
import play.api.libs.json.{JsError, JsPath, Json, JsonValidationError}

import scala.concurrent.duration.{Duration, FiniteDuration, _}
import scala.util.{Failure, Success, Try}

class DockerDuplicationEnvironment(variables: Map[String, String] = sys.env) {

  val defaultSourcePath: Path = Paths.get("/src")
  val defaultConfigFilePath: Path = Paths.get("/.codacyrc")

  val timeout: FiniteDuration = variables
    .get("TIMEOUT")
    .flatMap { rawDuration =>
      Try(Duration(rawDuration)).toOption.collect { case d: FiniteDuration => d }
    }
    .getOrElse(15.minutes)

  val isDebug: Boolean =
    variables.get("DEBUG").flatMap(rawDebug => Try(rawDebug.toBoolean).toOption).getOrElse(false)

  def configuration(configPath: Path = defaultSourcePath): Try[CodacyConfiguration] = {
    Try(Files.readAllBytes(configPath)).transform(
      raw => Try(Json.parse(raw)).flatMap(_.validate[CodacyConfiguration].fold(asFailure, conf => Success(conf))),
      _ => Try(CodacyConfiguration(None, None)))
  }

  private[this] def asFailure(error: Seq[(JsPath, Seq[JsonValidationError])]) =
    Failure(new Throwable(Json.stringify(JsError.toJson(error.toList))))

}

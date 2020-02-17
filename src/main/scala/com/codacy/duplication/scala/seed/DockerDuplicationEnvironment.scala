package com.codacy.duplication.scala.seed

import java.nio.file.{Files, Path, Paths}

import com.codacy.plugins.api.duplication.DuplicationTool._
import play.api.libs.json.{JsError, Json}

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import java.util.concurrent.TimeUnit

class DockerDuplicationEnvironment(variables: Map[String, String] = sys.env) {

  val defaultSourcePath: Path = Paths.get("/src")
  val defaultConfigFilePath: Path = Paths.get("/.codacyrc")

  val timeout: FiniteDuration = variables
    .get("TIMEOUT_SECONDS")
    .flatMap { timeoutSeconds =>
      Try(FiniteDuration(timeoutSeconds.toLong, TimeUnit.SECONDS)).toOption
    }
    .getOrElse(15.minutes)

  val isDebug: Boolean =
    variables.get("DEBUG").flatMap(rawDebug => Try(rawDebug.toBoolean).toOption).getOrElse(false)

  def configuration(configPath: Path = defaultSourcePath): Try[CodacyConfiguration] = {
    Try(Files.readAllBytes(configPath)).transform(
      raw =>
        Try(Json.parse(raw)).flatMap(_.validate[CodacyConfiguration]
          .fold(error => Failure(new Throwable(Json.stringify(JsError.toJson(error.toList)))), conf => Success(conf))),
      _ => Try(CodacyConfiguration(None, None)))
  }

}

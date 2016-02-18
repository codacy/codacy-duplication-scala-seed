package codacy.dockerApi

import java.nio.file.{Files, Paths}

import codacy.dockerApi.api.CodacyConfiguration
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, Json}

import scala.util.{Failure, Success, Try}

object DockerEnvironment {

  def config: Try[CodacyConfiguration] = Try(Files.readAllBytes(configFilePath)).transform(
    raw => Try(Json.parse(raw)).flatMap(
      _.validate[CodacyConfiguration].fold(
        asFailure,
        conf => Success(conf)
      )),
    error => Failure(error)
  )

  private[this] def asFailure(error: Seq[(JsPath, Seq[ValidationError])]) =
    Failure(new Throwable(Json.stringify(JsError.toFlatJson(error.toList))))

  private[this] lazy val configFilePath = sourcePath.resolve(".codacyrc")

  lazy val sourcePath = Paths.get("/src")
}

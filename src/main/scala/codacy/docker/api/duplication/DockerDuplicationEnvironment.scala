package codacy.docker.api.duplication

import java.nio.file.{Files, Path, Paths}

import better.files.File
import codacy.docker.api.{CodacyConfiguration, DuplicationConfiguration}
import play.api.libs.json.{JsError, JsPath, Json, JsonValidationError}

import scala.concurrent.duration._
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.{Failure, Success, Try}

class DockerDuplicationEnvironment(variables: Map[String, String] = sys.env) {

  val sourcePath: Path = Paths.get("/src")
  private val dockerRootPath: Path = File.root.path
  private val configFilePath = sourcePath.resolve(".codacyrc")
  private val dockerRootConfigFilePath = dockerRootPath.resolve(".codacyrc")

  val timeout: FiniteDuration = variables
    .get("DUPLICATION_TIMEOUT")
    .flatMap { rawDuration =>
      Try(Duration(rawDuration)).toOption.collect { case d: FiniteDuration => d }
    }
    .getOrElse(10.minutes)

  val isDebug: Boolean =
    variables.get("DUPLICATION_DEBUG").flatMap(rawDebug => Try(rawDebug.toBoolean).toOption).getOrElse(false)

  def config(configPath: Path = configFilePath,
             alternateConfigPath: Path = dockerRootConfigFilePath): Try[CodacyConfiguration] = {
    val rawConfig: Try[Array[Byte]] = getConfigurationFromFile(configPath, alternateConfigPath)
    rawConfig.transform(
      raw => Try(Json.parse(raw)).flatMap(_.validate[CodacyConfiguration].fold(asFailure, conf => Success(conf))),
      _ => Try(CodacyConfiguration(DuplicationConfiguration(None, None))))
  }
  private def getConfigurationFromFile(configPath: Path, alternateConfigPath: Path): Try[Array[Byte]] = {
    Try(Files.readAllBytes(configPath)).recoverWith {
      case _ =>
        Try(Files.readAllBytes(alternateConfigPath))
    }
  }

  private[this] def asFailure(error: Seq[(JsPath, Seq[JsonValidationError])]) =
    Failure(new Throwable(Json.stringify(JsError.toJson(error.toList))))

}

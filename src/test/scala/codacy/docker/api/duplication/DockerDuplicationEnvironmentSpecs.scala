package codacy.docker.api.duplication

import better.files.File
import codacy.docker.api.{CodacyConfiguration, DuplicationConfiguration}
import com.codacy.api.dtos.Languages
import org.specs2.mutable.Specification
import play.api.libs.json.Json

import scala.collection.immutable.HashMap
import scala.util.Try

class DockerDuplicationEnvironmentSpecs extends Specification {

  "DockerDuplicationEnvironment" should {

    val dockerDuplicationEnvironment = new DockerDuplicationEnvironment

    "get the duplication configuration for the tool, given a valid json file (must read from main source)" in {
      //given
      (for {
        tempFile <- File.temporaryFile()
      } yield {
        val duplicationConfiguration =
          CodacyConfiguration(
            DuplicationConfiguration(Some(Languages.Scala),
                                     Some(HashMap(
                                       DuplicationConfiguration.Key("ping") -> DuplicationConfiguration.Value("pong"),
                                       DuplicationConfiguration.Key("foo") -> DuplicationConfiguration.Value("bar")))))
        tempFile.write(Json.stringify(Json.toJson(duplicationConfiguration)))

        val configPath = tempFile.path
        val notConfigPath = tempFile.path.getRoot
        //when
        val duplicationConfig: Try[CodacyConfiguration] =
          dockerDuplicationEnvironment.config(configPath, notConfigPath)

        //then
        duplicationConfig must beSuccessfulTry[CodacyConfiguration](duplicationConfiguration)
      }).get()
    }

    "get the duplication configuration for the tool, given a valid json file (must read from alternative source)" in {
      //given
      (for {
        tempFile <- File.temporaryFile()
      } yield {
        val duplicationConfiguration =
          CodacyConfiguration(
            DuplicationConfiguration(Some(Languages.Scala),
                                     Some(HashMap(
                                       DuplicationConfiguration.Key("ping") -> DuplicationConfiguration.Value("pong"),
                                       DuplicationConfiguration.Key("foo") -> DuplicationConfiguration.Value("bar")))))
        tempFile.write(Json.stringify(Json.toJson(duplicationConfiguration)))

        val configPath = tempFile.path
        val notConfigPath = tempFile.path.getRoot
        //when
        val duplicationConfig: Try[CodacyConfiguration] =
          dockerDuplicationEnvironment.config(notConfigPath, configPath)

        //then
        duplicationConfig must beSuccessfulTry[CodacyConfiguration](duplicationConfiguration)
      }).get()
    }

    "fail getting the configuration, if the json is not valid" in {
      //given
      (for {
        tempFile <- File.temporaryFile()
      } yield {
        tempFile.write("{{invalid json}")

        val configPath = tempFile.path
        val notConfigPath = tempFile.path.getRoot
        //when
        val duplicationConfig =
          dockerDuplicationEnvironment.config(configPath, notConfigPath)

        //then
        duplicationConfig must beFailedTry
      }).get()
    }

    "get empty configuration if the configuration file doesn't exist" in {
      //given
      val nonExistentFile = File("nonExistentFile.nop")
      val srcFolder = File.currentWorkingDirectory

      //when
      val duplicationConfig =
        dockerDuplicationEnvironment.config(nonExistentFile.path, srcFolder.path)

      //then
      duplicationConfig must beSuccessfulTry[CodacyConfiguration](
        CodacyConfiguration(DuplicationConfiguration(None, None)))
    }
  }
}

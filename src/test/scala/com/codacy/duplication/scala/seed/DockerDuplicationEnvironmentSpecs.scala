package com.codacy.duplication.scala.seed

import better.files.File
import com.codacy.plugins.api.Options
import com.codacy.plugins.api.duplication.DuplicationTool.CodacyConfiguration
import com.codacy.plugins.api.languages.Languages
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
            Some(Languages.Scala),
            Some(Map(Options.Key("ping") -> Options.Value("pong"), Options.Key("foo") -> Options.Value("bar"))))
        tempFile.write(Json.stringify(Json.toJson(duplicationConfiguration)))

        val configPath = tempFile.path
        //when
        val duplicationConfig: Try[CodacyConfiguration] =
          dockerDuplicationEnvironment.configuration(configPath)

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
            Some(Languages.Scala),
            Some(HashMap(Options.Key("ping") -> Options.Value("pong"), Options.Key("foo") -> Options.Value("bar"))))
        tempFile.write(Json.stringify(Json.toJson(duplicationConfiguration)))

        val configPath = tempFile.path
        //when
        val duplicationConfig: Try[CodacyConfiguration] =
          dockerDuplicationEnvironment.configuration(configPath)

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
        //when
        val duplicationConfig =
          dockerDuplicationEnvironment.configuration(configPath)

        //then
        duplicationConfig must beFailedTry
      }).get()
    }

    "get empty configuration if the configuration file doesn't exist" in {
      //given
      val nonExistentFile = File("nonExistentFile.nop")

      //when
      val duplicationConfig =
        dockerDuplicationEnvironment.configuration(nonExistentFile.path)

      //then
      duplicationConfig must beSuccessfulTry[CodacyConfiguration](CodacyConfiguration(None, None))
    }
  }
}

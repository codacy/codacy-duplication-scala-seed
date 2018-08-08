package com.codacy.duplication.scala.seed

import better.files.File
import com.codacy.plugins.api.Implicits._
import com.codacy.plugins.api.Options
import com.codacy.plugins.api.duplication.DuplicationTool
import com.codacy.plugins.api.languages.Languages
import org.specs2.mutable.Specification

import scala.util.Try

class DockerDuplicationEnvironmentSpecs extends Specification {

  "DockerDuplicationEnvironment" should {

    val dockerDuplicationEnvironment = new DockerDuplicationEnvironment
    val duplicationConfiguration =
      DuplicationTool.CodacyConfiguration(
        Some(Languages.Scala),
        Some(Map(Options.Key("ping") -> Options.Value("pong"), Options.Key("foo") -> Options.Value("bar"))))
    val duplicationConfigurationJsonStr = """{"language":"Scala","params":{"ping":"pong","foo":"bar"}}"""

    "get the duplication configuration for the tool, given a valid json file (must read from main source)" in {
      //given
      (for {
        tempFile <- File.temporaryFile()
      } yield {

        tempFile.write(duplicationConfigurationJsonStr)

        val configPath = tempFile.path
        val notConfigPath = tempFile.path.getRoot
        //when
        val duplicationConfig: Try[DuplicationTool.CodacyConfiguration] =
          dockerDuplicationEnvironment.config(configPath, notConfigPath)

        //then
        duplicationConfig must beSuccessfulTry[DuplicationTool.CodacyConfiguration](duplicationConfiguration)
      }).get()
    }

    "get the duplication configuration for the tool, given a valid json file (must read from alternative source)" in {
      //given
      (for {
        tempFile <- File.temporaryFile()
      } yield {

        tempFile.write(duplicationConfigurationJsonStr)

        val configPath = tempFile.path
        val notConfigPath = tempFile.path.getRoot
        //when
        val duplicationConfig: Try[DuplicationTool.CodacyConfiguration] =
          dockerDuplicationEnvironment.config(notConfigPath, configPath)

        //then
        duplicationConfig must beSuccessfulTry[DuplicationTool.CodacyConfiguration](duplicationConfiguration)
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
      duplicationConfig must beSuccessfulTry[DuplicationTool.CodacyConfiguration](
        DuplicationTool.CodacyConfiguration(None, None))
    }
  }
}

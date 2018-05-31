package codacy.docker.api.duplication

import java.io.{ByteArrayOutputStream, PrintStream}

import codacy.docker.api.utils.{FileHelper, ResultsPrinter}
import codacy.docker.api.{DuplicationConfiguration, Source}
import com.codacy.api.dtos.Language
import org.specs2.mutable.Specification
import play.api.libs.json.Json

import scala.concurrent.TimeoutException
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success, Try}

class DockerDuplicationSpecs extends Specification {

  "DockerDuplication" should {
    "print the duplication clones results to the given stream and exit with the code 0" in {
      //given
      val outContent = new ByteArrayOutputStream()
      val printStream = new PrintStream(outContent)
      val dockerMetricsEnvironment = new DockerDuplicationEnvironment
      val sourcePath = dockerMetricsEnvironment.sourcePath.toString
      val duplication = "heeyyy, i'm duplicated"
      val duplicationCloneMock =
        DuplicationClone(duplication,
                         duplication.length,
                         1,
                         Seq(DuplicationCloneFile(s"$sourcePath/path/to/duplicated/file", 1, 2)))
      val mockedDuplicationTool = new DuplicationTool {
        override def apply(
          source: Source.Directory,
          language: Option[Language],
          options: Map[DuplicationConfiguration.Key, DuplicationConfiguration.Value]): Try[List[DuplicationClone]] = {
          Success(List(duplicationCloneMock))
        }
      }

      val dockerDuplication =
        new DockerDuplication(tool = mockedDuplicationTool)(printer = new ResultsPrinter(resultsStream = printStream)) {
          override def halt(status: Int): Unit = {
            status must beEqualTo(0)
            ()
          }
        }

      //when
      dockerDuplication.main(Array.empty)

      //then
      val output = Json.parse(outContent.toString)
      val mockedResults = Json.toJson(duplicationCloneMock.copy(files = duplicationCloneMock.files.map(file =>
        file.copy(FileHelper.stripPath(file.filePath, sourcePath)))))
      output mustEqual mockedResults
    }

    "fail if the apply method fails, print the stacktrace to the given stream and exit with the code 1" in {
      //given
      val outContent = new ByteArrayOutputStream()
      val printStream = new PrintStream(outContent)
      val failedMsg = s"Failed: ${Random.nextInt()}"
      val mockedDuplicationTool = new DuplicationTool {
        override def apply(
          source: Source.Directory,
          language: Option[Language],
          options: Map[DuplicationConfiguration.Key, DuplicationConfiguration.Value]): Try[List[DuplicationClone]] = {
          Failure(new Throwable(failedMsg))
        }
      }
      val dockerMetrics =
        new DockerDuplication(tool = mockedDuplicationTool)(printer = new ResultsPrinter(logStream = printStream)) {
          override def halt(status: Int): Unit = {
            status must beEqualTo(1)
            ()
          }
        }

      //when
      dockerMetrics.main(Array.empty)

      //then
      val output = outContent.toString
      output must contain(failedMsg)
    }

    "fail if the configured timeout on the system environment is too low" in {
      //given
      val outContent = new ByteArrayOutputStream()
      val printStream = new PrintStream(outContent)
      val timeOutValue = "2 seconds"
      val timeOutException = new TimeoutException(s"Duplication tool timed out after: $timeOutValue")
      val environment = new DockerDuplicationEnvironment(Map("DUPLICATION_TIMEOUT" -> timeOutValue))
      val duplicationTool = new DuplicationTool {
        def apply(
          source: Source.Directory,
          language: Option[Language],
          options: Map[DuplicationConfiguration.Key, DuplicationConfiguration.Value]): Try[List[DuplicationClone]] = {
          Thread.sleep(3.seconds.toMillis)
          Success(List.empty)
        }
      }
      val dockerMetrics =
        new DockerDuplication(tool = duplicationTool, environment = environment)(
          printer = new ResultsPrinter(logStream = printStream)) {
          override def halt(status: Int): Unit = {
            throw timeOutException
          }
        }

      //when and then
      dockerMetrics.main(Array.empty) must throwA(timeOutException)
    }
  }
}

package codacy.docker.api.utils

import java.io.{ByteArrayOutputStream, PrintStream}

import codacy.docker.api.duplication.{DockerDuplicationEnvironment, DuplicationClone, DuplicationCloneFile}
import org.specs2.mutable.Specification
import play.api.libs.json.Json

class ResultsPrinterSpecs extends Specification {

  "ResultsPrinter" should {
    "print the file metrics converted to json to the given print stream" in {
      //given
      val outContent = new ByteArrayOutputStream()
      val printStream = new PrintStream(outContent)
      val printer = new ResultsPrinter(printStream)
      val dockerMetricsEnvironment = new DockerDuplicationEnvironment
      val sourcePath = dockerMetricsEnvironment.sourcePath.toString
      val duplication = "heeyyy, i'm duplicated"
      val duplicationClone =
        DuplicationClone(duplication,
                         duplication.length,
                         1,
                         Seq(DuplicationCloneFile(s"$sourcePath/path/to/duplicated/file", 1, 2)))

      //when
      printer.printResults(List(duplicationClone), sourcePath)

      //then
      Json.parse(outContent.toString) mustEqual Json.toJson(
        duplicationClone.copy(files = duplicationClone.files.map(file =>
          file.copy(FileHelper.stripPath(file.filePath, sourcePath)))))
    }
  }
}

package com.codacy.duplication.scala.seed

import java.io.{ByteArrayOutputStream, PrintStream}

import com.codacy.duplication.scala.seed.utils.FileHelper
import com.codacy.plugins.api.duplication.{DuplicationClone, DuplicationCloneFile}
import org.specs2.mutable.Specification
import play.api.libs.json.Json

class ResultsPrinterSpecs extends Specification {

  "ResultsPrinter" should {
    "print the duplication results converted to json to the given print stream" in {
      //given
      val outContent = new ByteArrayOutputStream()
      val printStream = new PrintStream(outContent)
      val printer = new ResultsPrinter(printStream)
      val dockerDuplicationEnvironment = new DockerDuplicationEnvironment
      val sourcePath = dockerDuplicationEnvironment.defaultSourcePath.toString
      val duplication = "heeyyy, i'm duplicated"
      val duplicationClone =
        DuplicationClone(duplication,
                         duplication.length,
                         1,
                         Seq(DuplicationCloneFile(s"$sourcePath/path/to/duplicated/file", 1, 2)))

      //when
      printer.printResults(List(duplicationClone), sourcePath)

      //then
      val outputedJson = Json.parse(outContent.toString)
      val expectedJson = Json.toJson(duplicationClone.copy(files = duplicationClone.files.map(file =>
        file.copy(FileHelper.stripPath(file.filePath, sourcePath)))))
      outputedJson mustEqual expectedJson
    }
  }
}

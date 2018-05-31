package codacy.docker.api.utils

import java.io.PrintStream

import codacy.docker.api.duplication.DuplicationClone
import play.api.libs.json.Json

class ResultsPrinter(resultsStream: PrintStream = Console.out,
                     logStream: PrintStream = Console.err,
                     isDebug: Boolean = false) {

  def log(message: String): Unit = {
    if (isDebug) {
      logStream.println(message)
    }
  }

  def logStackTrace(error: Throwable): Unit = {
    error.printStackTrace(logStream)
  }

  def printResults(results: Seq[DuplicationClone], sourcePath: String): Unit = {

    results.foreach { result =>
      resultsStream.println(Json.stringify(Json.toJson(result.copy(files = result.files.map(file =>
        file.copy(filePath = stripSourcePath(file.filePath, sourcePath)))))))
    }
  }

  private def stripSourcePath(fullPath: String, sourcePath: String): String = {
    FileHelper.stripPath(fullPath, sourcePath)
  }
}

package com.codacy.duplication.scala.seed

import java.io.PrintStream

import com.codacy.duplication.scala.seed.utils.FileHelper
import com.codacy.plugins.api.Implicits._
import com.codacy.plugins.api.docker.v2.DuplicationResult
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

  def logStackTrace(stackTrace: String): Unit = {
    logStream.println(stackTrace)
  }

  private def logResult(result: DuplicationResult): Unit = {
    resultsStream.println(Json.stringify(Json.toJson(result)))
  }

  def printResults(results: List[DuplicationResult], sourcePath: String): Unit = {
    val relativizedResults: List[DuplicationResult] = results.map {
      case result: DuplicationResult.Clone =>
        result.copy(
          files = result.files.map(file => file.copy(filePath = FileHelper.stripPath(file.filePath, sourcePath))))
      case result => result
    }
    relativizedResults.foreach(logResult)
  }
}

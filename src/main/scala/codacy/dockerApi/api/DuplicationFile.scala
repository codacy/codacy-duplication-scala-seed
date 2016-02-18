package codacy.dockerApi.api

import codacy.dockerApi.traits.JsonEnumeration
import play.api.libs.json.{JsValue, Json}

case object Language extends Enumeration with JsonEnumeration {
  val Javascript, Scala, CSS, PHP, C, CPP, ObjectiveC, Python, Ruby, Perl, Java, CSharp, VisualBasic, Go, Elixir, Clojure,
  CoffeeScript, Rust, Swift, Haskell, React, Shell, TypeScript = Value

  def getExtensions(value: Value): List[String] = {
    value match {
      case Javascript => List(".js")
      case Scala => List(".scala")
      case CSS => List(".css")
      case PHP => List(".php")
      case C => List(".c", ".h")
      case CPP => List(".cpp", ".hpp")
      case ObjectiveC => List(".m")
      case Python => List(".py")
      case Ruby => List(".rb")
      case Perl => List(".pl")
      case Java => List(".java")
      case CSharp => List(".cs")
      case VisualBasic => List(".vb")
      case Go => List(".go")
      case Elixir => List(".ex", ".exs")
      case Clojure => List(".clj", ".cljs", ".cljc", ".edn")
      case CoffeeScript => List(".coffee")
      case Rust => List(".rs", ".rlib")
      case Swift => List(".swift")
      case Haskell => List(".hs", ".lhs")
      case React => List(".jsx")
      case Shell => List(".sh")
      case TypeScript => List(".ts")
      case _ => List.empty
    }
  }
}

case class DuplicationRequest(directory: String, files: List[String])

case class DuplicationCloneFile(filePath: String, startLine: Int, endLine: Int)

case class DuplicationClone(cloneLines: String, nrTokens: Int, nrLines: Int, files: Seq[DuplicationCloneFile])

case class DuplicationConfiguration(language: Language.Value, params: Map[String, JsValue])

case class CodacyConfiguration(duplication: DuplicationConfiguration)

object DuplicationClone extends DuplicationFormatters

object DuplicationConfiguration extends DuplicationFormatters

object CodacyConfiguration extends DuplicationFormatters

trait DuplicationFormatters {
  implicit val dupReqFmt = Json.format[DuplicationRequest]
  implicit val dupCloneFileFmt = Json.format[DuplicationCloneFile]
  implicit val dupCloneFmt = Json.format[DuplicationClone]
  implicit val dupCfgFmt = Json.format[DuplicationConfiguration]
  implicit val codacyCfgFmt = Json.format[CodacyConfiguration]
}

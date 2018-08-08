package com.codacy.plugins.api

import com.codacy.plugins.api.Implicits._
import com.codacy.plugins.api.docker.v2.{DuplicationResult, Problem}
import org.specs2.mutable.Specification
import play.api.libs.json.{JsSuccess, Json}

import scala.concurrent.duration._

class ImplicitsSpecs extends Specification {

  "Implicit conversions" should {

    val missingConfigJsonString =
      s"""{"message":"this is a message","reason":{"supportedFilename":["batato"],"$$type":"com.codacy.plugins.api.docker.v2.Problem$$Reason$$MissingConfiguration"},"$$type":"com.codacy.plugins.api.docker.v2.DuplicationResult$$Problem"}"""
    val missingConfigDuplicationProblem: DuplicationResult =
      DuplicationResult
        .Problem(ErrorMessage("this is a message"), None, Problem.Reason.MissingConfiguration(Set("batato")))

    val timeOutJsonString =
      s"""{"message":"this is a message","reason":{"timeout":{"length":10,"unit":"SECONDS"},"$$type":"com.codacy.plugins.api.docker.v2.Problem$$Reason$$TimedOut"},"$$type":"com.codacy.plugins.api.docker.v2.DuplicationResult$$Problem"}"""
    val timeOutDuplicationProblem: DuplicationResult =
      DuplicationResult.Problem(ErrorMessage("this is a message"), None, Problem.Reason.TimedOut(10.seconds))

    "deserialize DuplicationProblem with MissingConfiguration reason" in {
      Json.parse(missingConfigJsonString).validate[DuplicationResult] should beLike {
        case JsSuccess(x, _) => x should beEqualTo(missingConfigDuplicationProblem)
      }
    }

    "serialize DuplicationProblem with MissingConfiguration reason" in {
      Json.stringify(Json.toJson(missingConfigDuplicationProblem)) shouldEqual missingConfigJsonString
    }

    "deserialize DuplicationProblem with TimeOut reason" in {
      Json.parse(timeOutJsonString).asOpt[DuplicationResult] should beSome(timeOutDuplicationProblem)
    }

    "serialize DuplicationProblem with TimeOut reason" in {
      Json.stringify(Json.toJson(timeOutDuplicationProblem)) shouldEqual timeOutJsonString
    }
  }

}

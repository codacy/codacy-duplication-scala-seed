package codacy.dockerApi.traits

import java.nio.file.Path

import codacy.dockerApi.api.{DuplicationClone, DuplicationConfiguration}

import scala.util.Try

trait IDuplicationImpl {
  def apply(path: Path, config: DuplicationConfiguration): Try[List[DuplicationClone]]
}

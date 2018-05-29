package codacy.docker.api.utils

trait Haltable {

  def halt(status: Int): Unit = {
    Runtime.getRuntime.halt(status)
  }

}

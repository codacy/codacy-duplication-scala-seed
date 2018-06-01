package codacy.docker.api.utils

import java.util.{Timer, TimerTask}

import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Promise}
import scala.util.Try

trait Timeoutable {

  def onTimeout[T](delay: Duration)(onTimeout: => T): Future[T] = {
    val promise = Promise[T]()
    val t = new Timer()
    t.schedule(new TimerTask {
      override def run(): Unit = {
        promise.complete(Try(onTimeout))
      }
    }, delay.toMillis)
    promise.future
  }

}

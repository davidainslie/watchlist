package com.backwards.watchlist.routes

import scala.language.higherKinds
import cats.effect.IO
import org.http4s.{EntityDecoder, Response}
import org.scalatest.TestSuite

trait RoutesFixtureIO {
  this: TestSuite =>

  def responseAs[A](r: IO[Response[IO]])(implicit D: EntityDecoder[IO, A]): (Response[IO], A) = {
    val rr = r.unsafeRunSync
    val entity = rr.as[A].unsafeRunSync

    (rr, entity)
  }
}
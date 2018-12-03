package com.backwards.watchlist.routes

import scala.language.higherKinds
import cats.effect.IO
import org.http4s.{EntityDecoder, Response}
import org.scalatest.{Assertion, TestSuite}

trait RoutesFixtureIO {
  this: TestSuite =>

  def assert(r: IO[Assertion]): Assertion = r.unsafeRunSync

  def assert[A](r: IO[Response[IO]])(implicit D: EntityDecoder[IO, A]): (Response[IO], A) = {
    assert[A](r.unsafeRunSync)
  }

  def assert[A](r: Response[IO])(implicit D: EntityDecoder[IO, A]): (Response[IO], A) = {
    val entity = r.as[A].unsafeRunSync

    (r, entity)
  }
}
package com.backwards.watchlist

import scala.language.postfixOps
import cats.effect._
import cats.effect.internals.IOContextShift
import org.scalatest.{MustMatchers, WordSpec}

class RunnerSpec extends WordSpec with MustMatchers {
  implicit val contextShift: ContextShift[IO] = IOContextShift.global

  "Runner" should {
    "only run when requested i.e. conveys lazy evaluation" in {
      Runner.stream[IO]
      succeed
    }
  }
}
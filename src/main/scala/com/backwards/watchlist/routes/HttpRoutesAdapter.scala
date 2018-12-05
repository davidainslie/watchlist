package com.backwards.watchlist.routes

import scala.language.higherKinds
import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Request, Response}

abstract class HttpRoutesAdapter[F[_]: Sync, E](implicit HANDLER: HttpRoutesErrorHandler[F, E]) extends Http4sDsl[F] {
  val httpRoutes: PartialFunction[Request[F], F[Response[F]]] => HttpRoutes[F] = pf => {
    HANDLER(Kleisli(req => OptionT(implicitly[Sync[F]].suspend(pf.lift(req).sequence))))
  }
}
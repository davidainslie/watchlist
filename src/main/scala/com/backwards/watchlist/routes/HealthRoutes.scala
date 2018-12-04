package com.backwards.watchlist.routes

import scala.language.higherKinds
import cats.effect.Effect
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import com.backwards.http4s.circe.CirceOps

class HealthRoutes[F[_]: Effect] extends Http4sDsl[F] with CirceOps {
  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "healthz" =>
      Ok(SuccessResponse("Healthy"))
  }
}

object HealthRoutes {
  def apply[F[_]: Effect]: HttpRoutes[F] =
    new HealthRoutes[F].routes
}
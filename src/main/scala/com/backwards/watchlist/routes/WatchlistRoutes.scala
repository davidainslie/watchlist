package com.backwards.watchlist.routes

import scala.language.higherKinds
import cats.effect.Effect
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import com.backwards.http4s.circe.CirceOps
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.service.{ServiceError, WatchlistService}
import cats.implicits._
import cats.kernel.Eq
import eu.timepit.refined.api.RefType

// TODO - Maybe only need Sync instead of Effect
class WatchlistRoutes[F[_]: Effect](watchlistService: WatchlistService[F])(implicit PROXY: RoutesProxy[F, ServiceError]) extends Http4sDsl[F] with CirceOps {
  val routes: HttpRoutes[F] = PROXY(HttpRoutes.of[F] {
    case GET -> Root / "watchlist" / customerId =>
      CustomerId(customerId).fold(
        error => BadRequest(ErrorResponse(error)),
        customerId => watchlistService.watchlist(customerId).flatMap(Created(_))
      )
  })
}

object WatchlistRoutes {
  def apply[F[_]: Effect](watchlistService: WatchlistService[F])(implicit PROXY: RoutesProxy[F, ServiceError]): HttpRoutes[F] =
    new WatchlistRoutes[F](watchlistService).routes
}
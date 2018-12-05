package com.backwards.watchlist.routes

import scala.language.higherKinds
import cats.data.EitherT
import cats.effect.{Effect, Sync}
import cats.implicits._
import org.http4s.HttpRoutes
import com.backwards.http4s.circe.CirceOps
import com.backwards.watchlist.adt.Watchlist.ContentId
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.service.{ServiceError, WatchlistService}

class WatchlistRoutes[F[_]: Sync](watchlistService: WatchlistService[F])(implicit H: HttpRoutesErrorHandler[F, ServiceError]) extends HttpRoutesAdapter[F, ServiceError] with CirceOps {
  val routes: HttpRoutes[F] = httpRoutes {
    case GET -> Root / "watchlist" / customerId =>
      CustomerId(customerId).fold(
        error => BadRequest(ErrorResponse(error)),
        customerId => watchlistService.watchlist(customerId) >>= (Ok(_))
      )

    case request @ POST -> Root / "watchlist" / customerId =>
      CustomerId(customerId).fold(
        error => BadRequest(ErrorResponse(error)),
        customerId => request.as[Watchlist.Item] >>= watchlistService.add(customerId) >>= (Created(_))
      )

    case DELETE -> Root / "watchlist" / customerId / contentId =>
      val result: EitherT[F, String, Watchlist] = for {
        customerId <- EitherT.fromEither[F](CustomerId(customerId))
        contentId <- EitherT.fromEither[F](ContentId(contentId))
        watchlist <- EitherT.liftF(watchlistService.delete(customerId)(Watchlist.Item(contentId)))
      } yield watchlist

      result.value.flatMap {
        case Left(error) => BadRequest(ErrorResponse(error))
        case Right(watchlist) => Ok(watchlist)
      }
  }
}

object WatchlistRoutes {
  def apply[F[_]: Effect](watchlistService: WatchlistService[F])(implicit H: HttpRoutesErrorHandler[F, ServiceError]): HttpRoutes[F] =
    new WatchlistRoutes[F](watchlistService).routes
}
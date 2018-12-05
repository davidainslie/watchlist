package com.backwards.watchlist.routes

import scala.language.higherKinds
import cats.data.{Kleisli, OptionT}
import cats.implicits._
import cats.{ApplicativeError, MonadError}
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Request, Response}
import com.backwards.watchlist.service.{NonExistingCustomer, ServiceError}

trait HttpRoutesErrorHandler[F[_], E] {
  def apply(routes: HttpRoutes[F]): HttpRoutes[F]
}

object HttpRoutesErrorHandler {
  def apply[F[_], E](routes: HttpRoutes[F])(errorResponse: E => F[Response[F]])(implicit ev: ApplicativeError[F, E]): HttpRoutes[F] =
    Kleisli { request: Request[F] =>
      OptionT {
        routes.run(request).value.handleErrorWith { e => errorResponse(e).map(Option(_)) }
      }
    }
}

/**
  * Essentially translates service errors to HTTP errors
  */
class HttpRoutesServiceErrorHandler[F[_]](implicit M: MonadError[F, ServiceError]) extends HttpRoutesErrorHandler[F, ServiceError] with Http4sDsl[F] {
  private val errorResponse: ServiceError => F[Response[F]] = {
    case NonExistingCustomer(customerId) => NotFound(ErrorResponse(s"Non existing customer provided: $customerId"))
    // Etc.
  }

  override def apply(routes: HttpRoutes[F]): HttpRoutes[F] =
    HttpRoutesErrorHandler(routes)(errorResponse)
}
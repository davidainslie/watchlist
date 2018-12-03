package com.backwards.watchlist.routes

import scala.language.higherKinds
import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import cats.implicits._
import cats.{ApplicativeError, MonadError}
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Request, Response}
import com.backwards.watchlist.service.{NonExistingCustomer, ServiceError}
import io.circe.syntax._
import com.backwards.http4s.circe.CirceOps

trait RoutesProxy[F[_], E <: Throwable] {
  def apply(routes: HttpRoutes[F]): HttpRoutes[F]
}

object RoutesProxy {
  def apply[F[_], E <: Throwable](routes: HttpRoutes[F])(errorResponse: E => F[Response[F]])(implicit ev: ApplicativeError[F, E]): HttpRoutes[F] =
    Kleisli { request: Request[F] =>
      OptionT {
        routes.run(request).value.handleErrorWith { e => errorResponse(e).map(Option(_)) }
      }
    }
}

class ServiceErrorRoutesProxy[F[_]](implicit M: MonadError[F, ServiceError]) extends RoutesProxy[F, ServiceError] with Http4sDsl[F] {
  private val errorResponse: ServiceError => F[Response[F]] = {
    /*case InvalidUserAge(age) => BadRequest(s"Invalid age $age".asJson)
    case UserAlreadyExists(username) => Conflict(username.asJson)*/
    case NonExistingCustomer(customerId) => NotFound(ErrorResponse(s"Non existing customer provided: $customerId"))
  }

  override def apply(routes: HttpRoutes[F]): HttpRoutes[F] =
    RoutesProxy(routes)(errorResponse)
}
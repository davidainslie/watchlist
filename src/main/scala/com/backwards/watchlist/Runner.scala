package com.backwards.watchlist

import scala.language.higherKinds
import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import com.backwards.watchlist.repository.interpreter.InMemoryWatchlistRepository
import com.backwards.watchlist.routes.{HealthRoutes, HttpRoutesErrorHandler, HttpRoutesServiceErrorHandler, WatchlistRoutes}
import com.backwards.watchlist.service.ServiceError
import com.backwards.watchlist.service.interpreter.WatchlistServiceInterpreter
import com.olegpy.meow.hierarchy._

object Runner extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    println("===> Running...") // TODO Logging
    stream[IO]
  }

  def stream[F[_]: ConcurrentEffect]: F[ExitCode] = {
    implicit val httpRoutesServiceErrorHandler: HttpRoutesErrorHandler[F, ServiceError] = new HttpRoutesServiceErrorHandler[F]

    val healthRoutes = HealthRoutes[F]
    val watchlistRoutes = WatchlistRoutes[F](WatchlistServiceInterpreter[F](InMemoryWatchlistRepository[F]))

    val routes: HttpRoutes[F] = healthRoutes <+> watchlistRoutes

    val httpApp = Router("/" -> routes).orNotFound

    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
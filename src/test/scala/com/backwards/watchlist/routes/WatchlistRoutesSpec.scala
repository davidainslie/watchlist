package com.backwards.watchlist.routes

import cats.effect.IO
import io.circe.Json
import io.circe.literal._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Uri}
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.http4s.circe.CirceOps
import com.backwards.watchlist.adt.Watchlist
import com.backwards.watchlist.repository.interpreter.InMemoryWatchlistRepository
import com.backwards.watchlist.service.ServiceError
import com.backwards.watchlist.service.interpreter.WatchlistServiceInterpreter
import com.olegpy.meow.hierarchy._

class WatchlistRoutesSpec extends WordSpec with MustMatchers with Http4sDsl[IO] with Http4sClientDsl[IO] with RoutesFixtureIO with CirceOps {
  "Watchlist routes" should {
    "fail to get watchlist when an invalid customer ID is provided" in {
      implicit val serviceErrorRoutesProxy: RoutesProxy[IO, ServiceError] = new ServiceErrorRoutesProxy[IO]

      val routes: HttpRoutes[IO] = WatchlistRoutes[IO](WatchlistServiceInterpreter[IO](InMemoryWatchlistRepository[IO]))

      val (response, errorResponse) = responseAs[ErrorResponse](for {
        request <- GET(Uri.uri("/watchlist/invalidCustomerId"))
        response <- routes.orNotFound.run(request)
      } yield response)

      response.status mustBe BadRequest
      errorResponse.message must (include ("Predicate failed") and include ("invalidCustomerId"))
    }

    "fail to get watchlist for a non existing customer" in {
      implicit val serviceErrorRoutesProxy: RoutesProxy[IO, ServiceError] = new ServiceErrorRoutesProxy[IO]

      val routes: HttpRoutes[IO] = WatchlistRoutes[IO](WatchlistServiceInterpreter[IO](InMemoryWatchlistRepository[IO]))

      val (response, errorResponse) = responseAs[ErrorResponse](for {
        request <- GET(Uri.uri("/watchlist/123"))
        response <- routes.orNotFound.run(request)
      } yield response)

      response.status mustBe NotFound
      errorResponse mustBe ErrorResponse("Non existing customer for provided CustomerId(123)")
    }

    /*"acquire a customer's watchlist" in {
      implicit val serviceErrorRoutesProxy: RoutesProxy[IO, ServiceError] = new ServiceErrorRoutesProxy[IO]

      val routes: HttpRoutes[IO] = WatchlistRoutes[IO](WatchlistServiceInterpreter[IO](InMemoryWatchlistRepository[IO]))

      val (response, watchlist) = responseAs[Watchlist](for {
        request <- GET(Uri.uri("/watchlist/321"))
        response <- routes.orNotFound.run(request)
      } yield response)

      response.status mustBe Ok
      //watchlist mustBe ErrorResponse("Non existing customer for provided CustomerId(123)")
      println(s"===> WATCHLIST = $watchlist")
    }*/
  }

  "Watchlist JSON routes" should {
    "fail to get watchlist for a non existing customer - actually, a repeat of the above, just to double check the JSON (don't try this at home)" in {
      implicit val serviceErrorRoutesProxy: RoutesProxy[IO, ServiceError] = new ServiceErrorRoutesProxy[IO]

      val routes: HttpRoutes[IO] = WatchlistRoutes[IO](WatchlistServiceInterpreter[IO](InMemoryWatchlistRepository[IO]))

      val (response, json) = responseAs[Json](for {
        request <- GET(Uri.uri("/watchlist/123"))
        response <- routes.orNotFound.run(request)
      } yield response)

      response.status mustBe NotFound

      json"""{
        "error": {
          "message": "Non existing customer for provided CustomerId(123)"
        }
      }""" mustBe json
    }
  }
}
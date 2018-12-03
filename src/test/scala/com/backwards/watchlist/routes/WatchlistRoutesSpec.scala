package com.backwards.watchlist.routes

import cats.effect.IO
import cats.implicits._
import io.circe.Json
import io.circe.literal._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Uri}
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.http4s.circe.CirceOps
import com.backwards.watchlist.adt.Watchlist.ContentId
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.repository.interpreter.InMemoryWatchlistRepository
import com.backwards.watchlist.service.ServiceError
import com.backwards.watchlist.service.interpreter.WatchlistServiceInterpreter
import com.olegpy.meow.hierarchy._

class WatchlistRoutesSpec extends WordSpec with MustMatchers with Http4sDsl[IO] with Http4sClientDsl[IO] with RoutesFixtureIO with CirceOps {
  implicit val serviceErrorRoutesProxy: RoutesProxy[IO, ServiceError] = new ServiceErrorRoutesProxy[IO]

  "Watchlist routes" should {
    "fail to get watchlist when an invalid customer ID is provided" in {
      val routes: HttpRoutes[IO] = WatchlistRoutes[IO](WatchlistServiceInterpreter[IO](InMemoryWatchlistRepository[IO]))

      assert(for {
        request <- GET(Uri.uri("/watchlist/invalidCustomerId"))
        response <- routes.orNotFound.run(request)
      } yield {
        val (_, errorResponse) = assert[ErrorResponse](response)

        response.status mustBe BadRequest
        errorResponse.message must (include ("Predicate failed") and include ("invalidCustomerId"))
      })
    }

    "fail to get watchlist for a non existing customer" in {
      val routes: HttpRoutes[IO] = WatchlistRoutes[IO](WatchlistServiceInterpreter[IO](InMemoryWatchlistRepository[IO]))

      assert(for {
        request <- GET(Uri.uri("/watchlist/123"))
        response <- routes.orNotFound.run(request)
      } yield {
        val (_, errorResponse) = assert[ErrorResponse](response)

        response.status mustBe NotFound
        errorResponse mustBe ErrorResponse("Non existing customer provided: CustomerId(123)")
      })
    }

    "acquire a customer's watchlist" in {
      val watchlistRepository = InMemoryWatchlistRepository[IO]
      val routes: HttpRoutes[IO] = WatchlistRoutes[IO](WatchlistServiceInterpreter[IO](watchlistRepository))

      assert(for {
        customerId <- CustomerId("321").right.get.pure[IO]
        contentId <- ContentId("12345").right.get.pure[IO]
        watchlistItem = Watchlist.Item(contentId)
        _ <- watchlistRepository.add(customerId, watchlistItem)
        request <- GET(Uri.uri("/watchlist/321"))
        response <- routes.orNotFound.run(request)
      } yield {
        val (_, watchlist) = assert[Watchlist](response)

        response.status mustBe Created

        watchlist must have (
          'customerId (customerId),
          'items (Seq(watchlistItem))
        )
      })
    }
  }

  "Watchlist JSON routes" should {
    "fail to get watchlist for a non existing customer - actually, a repeat of the above, just to double check the JSON (don't try this at home folkd)" in {
      val routes: HttpRoutes[IO] = WatchlistRoutes[IO](WatchlistServiceInterpreter[IO](InMemoryWatchlistRepository[IO]))

      assert(for {
        request <- GET(Uri.uri("/watchlist/123"))
        response <- routes.orNotFound.run(request)
      } yield {
        val (_, jsonErrorResponse) = assert[Json](response)

        response.status mustBe NotFound

        json"""{
          "error": {
            "message": "Non existing customer provided: CustomerId(123)"
          }
        }""" mustBe jsonErrorResponse
      })
    }
  }
}
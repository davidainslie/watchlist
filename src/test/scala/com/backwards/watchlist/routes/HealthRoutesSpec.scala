package com.backwards.watchlist.routes

import cats.effect.IO
import org.http4s.Uri
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.http4s.circe.CirceOps

class HealthRoutesSpec extends WordSpec with MustMatchers with Http4sDsl[IO] with Http4sClientDsl[IO] with RoutesFixtureIO with CirceOps {
  "Health routes" should {
    "show that that all is ok" in {
      assert(for {
        request <- GET(Uri.uri("/healthz"))
        response <- HealthRoutes[IO].orNotFound.run(request)
      } yield {
        val (_, successResponse) = assert[SuccessResponse](response)

        response.status mustBe Ok
        successResponse.message mustBe "Healthy"
      })
    }
  }
}
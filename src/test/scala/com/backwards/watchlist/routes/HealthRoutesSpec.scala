package com.backwards.watchlist.routes

import cats.effect.IO
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.{Response, Uri}
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.http4s.circe.CirceOps
import com.backwards.watchlist.Data

class HealthRoutesSpec extends WordSpec with MustMatchers with Http4sDsl[IO] with Http4sClientDsl[IO] with CirceOps {
  "Health routes" should {
    "show that that all is ok" in {
      val v: IO[Response[IO]] = for {
        request <- GET(Uri.uri("/healthz"))
        response <- HealthRoutes[IO].orNotFound.run(request)
      } yield response

      // TODO - WIP
      println(v.unsafeRunSync.as[Data].unsafeRunSync)
    }
  }
}
package com.backwards.watchlist.service

import scala.language.higherKinds
import cats._
import cats.effect.IO
import cats.syntax.all._
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.effect.EvalOps
import com.backwards.spec.SpecOps
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.repository.interpreter.InMemoryWatchlistRepository
import com.backwards.watchlist.service.interpreter.WatchlistServiceInterpreter

class WatchlistServiceSpec extends WordSpec with MustMatchers with SpecOps {
  def watchlistService[F[_]: Applicative](implicit ME: MonadError[F, Throwable]): WatchlistService[F] =
    WatchlistServiceInterpreter[F](InMemoryWatchlistRepository[F])

  "Watchlist service with no effect such as Eval" should {
    "fail to get watchlist for an unknown customer" in new EvalOps {
      val service: WatchlistService[Eval] = watchlistService[Eval]

      for {
        customerId <- CustomerId("123")
        watchlistEval <- service.watchlist(customerId).asRight
      } yield
        intercept[NonExistingCustomer](watchlistEval.value).customerId mustBe customerId
    }
  }

  "Watchlist service with effect such as IO" should {
    "fail to get watchlist for an unknown customer" in {
      val service = watchlistService[IO]

      for {
        customerId <- CustomerId("123")
        watchlistIO <- service.watchlist(customerId).asRight
      } yield {
        def expectation: PartialFunction[Throwable Either Watchlist, Unit] = {
          case Left(t: NonExistingCustomer) => t.customerId mustBe customerId
        }

        watchlistIO.unsafeRunAsync(expectation orElse failed)
      }
    }
  }
}
package com.backwards.watchlist.service

import scala.language.higherKinds
import cats._
import cats.effect.IO
import cats.syntax.all._
import org.scalatest.{MustMatchers, OneInstancePerTest, WordSpec}
import com.backwards.effect.EvalOps
import com.backwards.spec.SpecOps
import com.backwards.watchlist.adt.Watchlist.ContentId
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.repository.interpreter.InMemoryWatchlistRepository
import com.backwards.watchlist.service.interpreter.WatchlistServiceInterpreter

class WatchlistServiceSpec extends WordSpec with MustMatchers with OneInstancePerTest with SpecOps with EvalOps {
  val service: WatchlistService[Eval] = watchlistService[Eval]

  def watchlistService[F[_]: Applicative](implicit ME: MonadError[F, Throwable]): WatchlistService[F] =
    WatchlistServiceInterpreter[F](InMemoryWatchlistRepository[F])

  "Watchlist service with no effect such as Eval" should {
    "add item to watchlist" in {
      for {
        customerId <- CustomerId("123")
        contentId <- ContentId("12345")
        item = Watchlist.Item(contentId)
        watchlist <- service.add(customerId)(item).asRight
      } yield
        watchlist.value must have (
          'customerId (customerId),
          'items (Seq(item))
        )
    }

    "delete item from watchlist" in {
      for {
        customerId <- CustomerId("123")
        contentId <- ContentId("12345")
        item = Watchlist.Item(contentId)
        _ <- service.add(customerId)(item).asRight
        watchlist <- service.delete(customerId)(item).asRight
      } yield
        watchlist.value must have (
          'customerId (customerId),
          'items (Nil)
        )
    }

    "fail to get watchlist for an unknown customer" in {
      for {
        customerId <- CustomerId("123")
        watchlist <- service.watchlist(customerId).asRight
      } yield
        intercept[NonExistingCustomer](watchlist.value).customerId mustBe customerId
    }

    "get watchlist" in {
      for {
        customerId <- CustomerId("123")
        contentId <- ContentId("12345")
        item = Watchlist.Item(contentId)
        _ <- service.add(customerId)(item).asRight
        watchlist <- service.watchlist(customerId).asRight
      } yield
        watchlist.value must have (
          'customerId (customerId),
          'items (Seq(item))
        )
    }
  }

  "Watchlist service with effect such as IO" should {
    "fail to get watchlist for an unknown customer" in {
      val service = watchlistService[IO]

      for {
        customerId <- CustomerId("123")
        watchlist <- service.watchlist(customerId).asRight
      } yield {
        def expectation: PartialFunction[Throwable Either Watchlist, Unit] = {
          case Left(t: NonExistingCustomer) => t.customerId mustBe customerId
        }

        watchlist.unsafeRunAsync(expectation orElse failed)
      }
    }
  }
}
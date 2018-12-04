package com.backwards.watchlist.repository

import cats.Id
import org.scalatest.{MustMatchers, OneInstancePerTest, WordSpec}
import com.backwards.watchlist.adt.Watchlist.ContentId
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.repository.interpreter.InMemoryWatchlistRepository

class WatchlistRepositorySpec extends WordSpec with MustMatchers with OneInstancePerTest {
  val repository: WatchlistRepository[Id] = InMemoryWatchlistRepository[Id]

  "In-memory watchlist repository" should {
    "fail to get watchlist for an unknown customer" in {
      CustomerId("123").map(repository.get).map(_ mustBe None)
    }

    "add an item to watchlist for a given customer ID" in {
      for {
        customerId <- CustomerId("123").toOption
        contentId <- ContentId("54321").toOption
        item = Watchlist.Item(contentId)
        watchlist <- repository.add(item, customerId)
      } yield
        watchlist must have (
          'customerId (customerId),
          'items (Seq(item))
        )
    }

    "get empty watchlist for a given customer ID" in {
      for {
        customerId <- CustomerId("123").toOption
        watchlist <- repository.get(customerId)
      } yield
        watchlist must have (
          'customerId (customerId),
          'items (Nil)
        )
    }

    "get watchlist for a given customer I" in {
      for {
        customerId <- CustomerId("123").toOption
        contentId <- ContentId("54321").toOption
        item = Watchlist.Item(contentId)
        _ <- repository.add(item, customerId)
        watchlist <- repository.get(customerId)
      } yield
        watchlist must have (
          'customerId (customerId),
          'items (Seq(item))
        )
    }

    "delete item from watchlist for a given customer I" in {
      for {
        customerId <- CustomerId("123").toOption
        contentId <- ContentId("54321").toOption
        item = Watchlist.Item(contentId)
        _ <- repository.delete(item, customerId)
        watchlist <- repository.get(customerId)
      } yield
        watchlist must have (
          'customerId (customerId),
          'items (Nil)
        )
    }
  }
}
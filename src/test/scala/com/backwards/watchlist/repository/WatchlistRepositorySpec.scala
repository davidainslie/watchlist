package com.backwards.watchlist.repository

import cats.Id
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.watchlist.adt.Watchlist.ContentId
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.repository.interpreter.InMemoryWatchlistRepository

class WatchlistRepositorySpec extends WordSpec with MustMatchers {
  "In-memory watchlist repository" should {
    "fail to get watchlist for an unknown customer" in {
      val repository = InMemoryWatchlistRepository[Id]

      CustomerId("123").map(repository.get).map(_ mustBe None)
    }

    "get watchlist for a given customer ID" in {
      val repository = InMemoryWatchlistRepository[Id]

      for {
        customerId <- CustomerId("123")
        contentId <- ContentId("54321")
        item = Watchlist.Item(contentId)
        watchlist <- repository.add(item, customerId) toRight "No watchlist"
      } yield {
        watchlist.customerId mustBe customerId

        val Seq(i) = watchlist.items
        i mustBe item
      }
    }
  }
}
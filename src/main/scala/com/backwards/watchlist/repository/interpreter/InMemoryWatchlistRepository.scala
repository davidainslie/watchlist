package com.backwards.watchlist.repository.interpreter

import scala.concurrent.stm._
import scala.language.higherKinds
import cats.Applicative
import cats.implicits._
import monocle.Lens
import monocle.macros.GenLens
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.repository.WatchlistRepository

class InMemoryWatchlistRepository[F[_]: Applicative] extends WatchlistRepository[F] {
  private val cache = TMap.empty[CustomerId, Watchlist]

  val itemsLens: Lens[Watchlist, Seq[Watchlist.Item]] =
    GenLens[Watchlist](_.items)

  def get(customerId: CustomerId): F[Option[Watchlist]] = atomic { implicit txn =>
    cache.get(customerId).pure[F]
  }

  def add(item: Watchlist.Item, customerId: CustomerId): F[Option[Watchlist]] = atomic { implicit txn =>
    val add: Watchlist.Item => Watchlist => Watchlist =
      item => itemsLens.modify(_ :+ item)

    val watchlist: Watchlist = add(item)(cache.getOrElse(customerId, Watchlist(customerId)))

    (cache += (customerId -> watchlist)).get(customerId).pure[F]
  }
}

object InMemoryWatchlistRepository {
  def apply[F[_]: Applicative]: InMemoryWatchlistRepository[F] =
    new InMemoryWatchlistRepository[F]
}
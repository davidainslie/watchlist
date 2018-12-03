package com.backwards.watchlist.repository.interpreter

import scala.collection.concurrent.TrieMap
import scala.language.higherKinds
import cats.Applicative
import cats.implicits._
import monocle.Lens
import monocle.macros.GenLens
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.repository.WatchlistRepository

class InMemoryWatchlistRepository[F[_]: Applicative] extends WatchlistRepository[F] {
  private val cache = new TrieMap[CustomerId, Watchlist]

  val itemsLens: Lens[Watchlist, Seq[Watchlist.Item]] =
    GenLens[Watchlist](_.items)

  val add: Watchlist.Item => Watchlist => Watchlist =
    item => itemsLens.modify(_ :+ item)

  def get(customerId: CustomerId): F[Option[Watchlist]] =
    cache.get(customerId).pure[F]

  def add(customerId: CustomerId, item: Watchlist.Item): F[Option[Watchlist]] =
    Option(add(item)(cache.getOrElseUpdate(customerId, Watchlist(customerId)))).pure[F]
}

object InMemoryWatchlistRepository {
  def apply[F[_]: Applicative]: InMemoryWatchlistRepository[F] =
    new InMemoryWatchlistRepository[F]
}
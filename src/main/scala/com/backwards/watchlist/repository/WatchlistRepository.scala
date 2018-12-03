package com.backwards.watchlist.repository

import scala.language.higherKinds
import com.backwards.watchlist.adt.{CustomerId, Watchlist}

trait WatchlistRepository[F[_]] {
  def get(customerId: CustomerId): F[Option[Watchlist]]

  def add(customerId: CustomerId, item: Watchlist.Item): F[Option[Watchlist]]
}
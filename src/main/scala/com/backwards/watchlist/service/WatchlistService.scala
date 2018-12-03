package com.backwards.watchlist.service

import scala.language.higherKinds
import com.backwards.watchlist.adt.{CustomerId, Watchlist}

trait WatchlistService[F[_]] {
  def watchlist(customerId: CustomerId): F[Watchlist]
}
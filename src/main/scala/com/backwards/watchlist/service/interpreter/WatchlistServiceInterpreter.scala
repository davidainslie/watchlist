package com.backwards.watchlist.service.interpreter

import scala.language.higherKinds
import cats.MonadError
import cats.implicits._
import com.backwards.watchlist.adt.{CustomerId, Watchlist}
import com.backwards.watchlist.repository.WatchlistRepository
import com.backwards.watchlist.service.{NonExistingCustomer, WatchlistService}

class WatchlistServiceInterpreter[F[_]](watchlistRepository: WatchlistRepository[F])(implicit F: MonadError[F, Throwable]) extends WatchlistService[F] {
  def watchlist(customerId: CustomerId): F[Watchlist] =
    for {
      wl <- watchlistRepository get customerId
      watchlist <- wl.fold(F.raiseError[Watchlist](NonExistingCustomer(customerId)))(F.pure)
    } yield watchlist

  def add(customerId: CustomerId)(item: Watchlist.Item): F[Watchlist] =
    for {
      wl <- watchlistRepository.add(item, customerId)
      watchlist <- wl.fold(F.raiseError[Watchlist](NonExistingCustomer(customerId)))(F.pure)
    } yield watchlist

  def delete(customerId: CustomerId)(item: Watchlist.Item): F[Watchlist] =
    for {
      wl <- watchlistRepository.delete(item, customerId)
      watchlist <- wl.fold(F.raiseError[Watchlist](NonExistingCustomer(customerId)))(F.pure)
    } yield watchlist
}

object WatchlistServiceInterpreter {
  def apply[F[_]](watchlistRepository: WatchlistRepository[F])(implicit F: MonadError[F, Throwable]): WatchlistService[F] =
    new WatchlistServiceInterpreter(watchlistRepository)
}
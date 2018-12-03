package com.backwards.watchlist.service

import com.backwards.watchlist.adt.CustomerId

sealed trait ServiceError extends Throwable

case class NonExistingCustomer(customerId: CustomerId) extends ServiceError
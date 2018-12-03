package com.backwards.watchlist.adt

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import com.backwards.watchlist.adt.CustomerId.ThreeAlphanumericCharacters

final case class Customer(id: CustomerId)

final case class CustomerId(value: String Refined ThreeAlphanumericCharacters)

object CustomerId {
  type ThreeAlphanumericCharacters = MatchesRegex[W.`"^([a-zA-Z0-9]){3}$"`.T]

  def apply(id: String): String Either CustomerId =
    refineV[ThreeAlphanumericCharacters](id).map(CustomerId.apply)
}
package com.backwards.watchlist.adt

import scala.language.higherKinds
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}
import com.backwards.watchlist.adt.Watchlist.ContentId.FiveAlphanumericCharacters
import cats.syntax.either._

final case class Watchlist(customerId: CustomerId, items: Seq[Watchlist.Item] = Nil)

// TODO Refinements are great, but this is the first time I tried to utilise automatic JSON encoding/decoding but didn't work.
// TODO As a result, I've coded encoders/decoders for refined types (I tried to avoid this by using implicits from io.circe.refined).
object Watchlist {
  implicit val watchlistEncoder: Encoder[Watchlist] =
    Encoder.forProduct2("customerId", "items")(w => (w.customerId.value.value, w.items))

  implicit val watchlistDecoder: Decoder[Watchlist] =
    (c: HCursor) => for {
      strCustomerId <- c.downField("customerId").as[String]
      customerId <- CustomerId(strCustomerId).leftMap(s => DecodingFailure(s, Nil))
      items <- c.downField("items").as[Seq[Item]]
    } yield Watchlist(customerId, items)

  final case class Item(contentId: ContentId)

  object Item {
    implicit val watchlistItemEncoder: Encoder[Item] =
      Encoder.forProduct1("contentId")(_.contentId.value.value)

    implicit val watchlistItemDecoder: Decoder[Item] = (c: HCursor) => {
      c.downField("contentId").as[String].flatMap(ContentId(_).leftMap(s => DecodingFailure(s, Nil))).map(Item.apply)
    }
  }

  final case class ContentId(value: String Refined FiveAlphanumericCharacters)

  object ContentId {
    import eu.timepit.refined._

    type FiveAlphanumericCharacters = MatchesRegex[W.`"^([a-zA-Z0-9]){5}$"`.T]

    def apply(id: String): String Either ContentId =
      refineV[FiveAlphanumericCharacters](id).map(ContentId.apply)
  }
}
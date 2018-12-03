package com.backwards.watchlist.routes

import scala.language.higherKinds
import cats.Applicative
import cats.effect.Sync
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class ErrorResponse(message: String)

object ErrorResponse {
  implicit val encoder: Encoder[ErrorResponse] = (e: ErrorResponse) => Json.obj(
    ("error", Json.obj(
      ("message", Json.fromString(e.message))
    ))
  )

  implicit val decoder: Decoder[ErrorResponse] =
    (c: HCursor) => for {
      message <- c.downField("error").downField("message").as[String]
    } yield ErrorResponse(message)

  implicit def entityEncoder[F[_]: Applicative]: EntityEncoder[F, ErrorResponse] = jsonEncoderOf[F, ErrorResponse]

  implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, ErrorResponse] = jsonOf[F, ErrorResponse]
}
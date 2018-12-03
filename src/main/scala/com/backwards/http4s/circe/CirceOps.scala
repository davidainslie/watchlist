package com.backwards.http4s.circe

import scala.language.higherKinds
import cats.effect.Sync
import io.circe._
import io.circe.generic.AutoDerivation
import io.circe.generic.extras.decoding.UnwrappedDecoder
import io.circe.generic.extras.encoding.UnwrappedEncoder
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

trait CirceOps extends AutoDerivation {
  implicit def valueClassEncoder[A: UnwrappedEncoder]: Encoder[A] = implicitly

  implicit def valueClassDecoder[A: UnwrappedDecoder]: Decoder[A] = implicitly

  implicit def jsonEncoder[F[_]: Sync, A <: Product: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

  implicit def jsonDecoder[F[_]: Sync, A <: Product: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]
}

object CirceOps extends CirceOps
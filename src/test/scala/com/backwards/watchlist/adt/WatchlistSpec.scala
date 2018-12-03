package com.backwards.watchlist.adt

import eu.timepit.refined._
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.watchlist.adt.Watchlist.ContentId
import com.backwards.watchlist.adt.Watchlist.ContentId.FiveAlphanumericCharacters

class WatchlistSpec extends WordSpec with MustMatchers with PropertyChecks {
  implicit override val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 1000)

  "Content Id" should {
    "not compile when accessed directly without correctly refined data" in {
      """new ContentId("12345")""" mustNot compile
    }

    "be validated when correctly refined" in {
      val id = refineMV[FiveAlphanumericCharacters]("12345")

      ContentId(id).value mustBe id
    }

    "be validated" in {
      val id = "12345"

      val Right(contentId) = ContentId(id)
      contentId.value.value mustBe id
    }

    "be invalidated for incorrect length" in {
      val id = "123"

      val Left(error) = ContentId(id)
      error must include(id)
    }

    "be invalidated given invalid characters" in {
      val id = "12^45"

      val Left(error) = ContentId(id)
      error must include(id)
    }
  }

  "Content Id property" should {
    "be validated" in {
      val gen = Gen.listOfN(5, Gen.alphaNumChar).map(_.mkString)

      forAll(gen) { s =>
        val Right(contentId) = ContentId(s)
        contentId.value.value mustBe s
      }
    }
  }
}
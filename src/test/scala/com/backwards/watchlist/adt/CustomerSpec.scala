package com.backwards.watchlist.adt

import eu.timepit.refined._
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.watchlist.adt.CustomerId.ThreeAlphanumericCharacters

class CustomerSpec extends WordSpec with MustMatchers with PropertyChecks {
  implicit override val generatorDrivenConfig: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 1000)

  "Customer Id" should {
    "not compile when accessed directly without correctly refined data" in {
      """new CustomerId("123")""" mustNot compile
    }

    "be validated when correctly refined" in {
      val id = refineMV[ThreeAlphanumericCharacters]("123")

      CustomerId(id).value mustBe id
    }

    "be validated" in {
      val id = "123"

      val Right(customerId) = CustomerId(id)
      customerId.value.value mustBe id
    }

    "be invalidated for incorrect length" in {
      val id = "12345"

      val Left(error) = CustomerId(id)
      error must include(id)
    }

    "be invalidated given invalid characters" in {
      val id = "1^3"

      val Left(error) = CustomerId(id)
      error must include(id)
    }
  }

  "Customer Id property" should {
    "be validated" in {
      val gen = Gen.listOfN(3, Gen.alphaNumChar).map(_.mkString)

      forAll(gen) { s =>
        val Right(customerId) = CustomerId(s)
        customerId.value.value mustBe s
      }
    }
  }
}
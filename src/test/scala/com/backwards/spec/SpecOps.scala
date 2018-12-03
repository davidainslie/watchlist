package com.backwards.spec

import org.scalatest.TestSuite

trait SpecOps {
  this: TestSuite =>

  def failed[I, R]: PartialFunction[I, R] = {
    case _ => fail()
  }
}
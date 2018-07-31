package com.cj.scala.fundamentals

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class WhyToUseLazyWhenInjectingDependencies extends FunSuite {

  val expectedInnerContent = "content"

  test("fine if everything is concrete") {
    val whenThingsHappened = new ArrayBuffer[String]()
    class Outer(val inner: String) {
      whenThingsHappened.append("outer created")
    }
    trait DependencyInjection {
      val inner: String = {
        whenThingsHappened.append("inner evaluated")
        expectedInnerContent
      }
      val outer = new Outer(inner)
    }
    val application = new DependencyInjection {}
    assert(application.outer.inner === expectedInnerContent)
    assert(whenThingsHappened === Seq("inner evaluated", "outer created"))
  }

  test("not good when something concrete depends on something abstract") {
    val whenThingsHappened = new ArrayBuffer[String]()
    class Outer(val inner: String) {
      whenThingsHappened.append("outer created")
    }
    trait DependencyInjection {
      val inner: String
      val outer = new Outer(inner)
    }
    val application = new DependencyInjection {
      override val inner: String = {
        whenThingsHappened.append("inner evaluated")
        expectedInnerContent
      }
    }
    // not initialized correctly
    assert(application.outer.inner !== expectedInnerContent)
    assert(whenThingsHappened === Seq("outer created", "inner evaluated"))
  }

  test("not good when something concrete depends on something abstract, even if indirectly") {
    val whenThingsHappened = new ArrayBuffer[String]()
    class Outer(val inner: String) {
      whenThingsHappened.append("outer created")
    }
    class BigOuter(val outer: Outer) {
      whenThingsHappened.append("big outer created")
    }
    trait DependencyInjection {
      val inner: String
      lazy val outer = new Outer(inner)
      val bigOuter = new BigOuter(outer)
    }
    val application = new DependencyInjection {
      override val inner: String = {
        whenThingsHappened.append("inner evaluated")
        expectedInnerContent
      }
    }
    // not initialized correctly
    assert(application.bigOuter.outer.inner !== expectedInnerContent)
    assert(whenThingsHappened === Seq("outer created", "big outer created", "inner evaluated"))
  }

  test("when something concrete depends on something abstract, make the concrete thing lazy") {
    val whenThingsHappened = new ArrayBuffer[String]()
    class Outer(val inner: String) {
      whenThingsHappened.append("outer created")
    }
    class BigOuter(val outer: Outer) {
      whenThingsHappened.append("big outer created")
    }
    trait DependencyInjection {
      val inner: String
      lazy val outer = new Outer(inner)
      lazy val bigOuter = new BigOuter(outer)
    }
    val application = new DependencyInjection {
      override val inner: String = {
        whenThingsHappened.append("inner evaluated")
        expectedInnerContent
      }
    }
    assert(application.bigOuter.outer.inner === expectedInnerContent)
    assert(whenThingsHappened === Seq("inner evaluated", "outer created", "big outer created"))
  }
}

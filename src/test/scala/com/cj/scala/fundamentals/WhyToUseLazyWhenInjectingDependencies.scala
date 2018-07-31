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
    class Middle(val inner: String) {
      whenThingsHappened.append("middle created")
    }
    class Outer(val middle: Middle) {
      whenThingsHappened.append("outer created")
    }
    trait DependencyInjection {
      val inner: String
      lazy val middle = new Middle(inner)
      val outer = new Outer(middle)
    }
    val application = new DependencyInjection {
      override val inner: String = {
        whenThingsHappened.append("inner evaluated")
        expectedInnerContent
      }
    }
    // not initialized correctly
    assert(application.outer.middle.inner !== expectedInnerContent)
    assert(whenThingsHappened === Seq("middle created", "outer created", "inner evaluated"))
  }

  test("when something concrete depends on something abstract, make the concrete thing lazy") {
    val whenThingsHappened = new ArrayBuffer[String]()
    class Middle(val inner: String) {
      whenThingsHappened.append("middle created")
    }
    class Outer(val middle: Middle) {
      whenThingsHappened.append("outer created")
    }
    trait DependencyInjection {
      val inner: String
      lazy val middle = new Middle(inner)
      lazy val outer = new Outer(middle)
    }
    val application = new DependencyInjection {
      override val inner: String = {
        whenThingsHappened.append("inner evaluated")
        expectedInnerContent
      }
    }
    assert(application.outer.middle.inner === expectedInnerContent)
    assert(whenThingsHappened === Seq("inner evaluated", "middle created", "outer created"))
  }
}

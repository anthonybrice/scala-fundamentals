package com.cj.scala.fundamentals

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class WhyToUseLazyWhenInjectingDependencies extends FunSuite {
  test("fine if everything is concrete") {
    val whenThingsHappened = new ArrayBuffer[String]()
    class Y(val z: String) {
      whenThingsHappened.append("y created")
    }
    trait DependencyInjection {
      val z: String = {
        whenThingsHappened.append("z evaluated")
        "blah"
      }
      val y = new Y(z)
    }
    val application = new DependencyInjection {}
    assert(application.y.z === "blah")
    assert(whenThingsHappened === Seq("z evaluated", "y created"))
  }

  test("not good when something concrete depends on something abstract") {
    val whenThingsHappened = new ArrayBuffer[String]()
    class Y(val z: String) {
      whenThingsHappened.append("y created")
    }
    trait DependencyInjection {
      val z: String
      val y = new Y(z)
    }
    val application = new DependencyInjection {
      override val z: String = {
        whenThingsHappened.append("z evaluated")
        "blah"
      }
    }
    assert(application.y.z === null)
    assert(whenThingsHappened === Seq("y created", "z evaluated"))
  }

  test("not good when something concrete depends on something abstract, even if indirectly") {
    val whenThingsHappened = new ArrayBuffer[String]()
    class Y(val z: String) {
      whenThingsHappened.append("y created")
    }
    class X(val y: Y) {
      whenThingsHappened.append("x created")
    }
    trait DependencyInjection {
      val z: String
      lazy val y = new Y(z)
      val x = new X(y)
    }
    val application = new DependencyInjection {
      override val z: String = {
        whenThingsHappened.append("z evaluated")
        "blah"
      }
    }
    assert(application.x.y.z === null)
    assert(whenThingsHappened === Seq("y created", "x created", "z evaluated"))
  }

  test("when something concrete depends on something abstract, make the concrete thing lazy") {
    val whenThingsHappened = new ArrayBuffer[String]()
    class Y(val z: String) {
      whenThingsHappened.append("y created")
    }
    class X(val y: Y) {
      whenThingsHappened.append("c created")
    }
    trait DependencyInjection {
      val z: String
      lazy val y = new Y(z)
      lazy val x = new X(y)
    }
    val application = new DependencyInjection {
      override val z: String = {
        whenThingsHappened.append("z evaluated")
        "blah"
      }
    }
    assert(application.x.y.z === "blah")
    assert(whenThingsHappened === Seq("z evaluated", "y created", "c created"))
  }
}

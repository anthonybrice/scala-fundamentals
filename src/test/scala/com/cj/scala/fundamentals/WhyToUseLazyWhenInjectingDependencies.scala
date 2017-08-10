package com.cj.scala.fundamentals

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class WhyToUseLazyWhenInjectingDependencies extends FunSuite {
  test("fine if everything is concrete") {
    val whenThingsHappened = new ArrayBuffer[String]()
    trait A {
      val x: String = {
        whenThingsHappened.append("x evaluated")
        "blah"
      }
      val b = new B(x)
    }
    class B(val x: String) {
      whenThingsHappened.append("b created")
    }
    val a = new A {}
    assert(a.b.x === "blah")
    assert(whenThingsHappened === Seq("x evaluated", "b created"))
  }

  test("not good when something concrete depends on something abstract") {
    val whenThingsHappened = new ArrayBuffer[String]()
    trait A {
      val x: String
      val b = new B(x)
    }
    class B(val x: String) {
      whenThingsHappened.append("b created")
    }
    val a = new A {
      override val x: String = {
        whenThingsHappened.append("x evaluated")
        "blah"
      }
    }
    assert(a.b.x === null)
    assert(whenThingsHappened === Seq("b created", "x evaluated"))
  }

  test("not good when something concrete depends on something abstract, even if indirectly") {
    val whenThingsHappened = new ArrayBuffer[String]()
    trait A {
      val x: String
      lazy val b = new B(x)
      val c = new C(b)
    }
    class B(val x: String) {
      whenThingsHappened.append("b created")
    }
    class C(val b: B) {
      whenThingsHappened.append("c created")
    }
    val a = new A {
      override val x: String = {
        whenThingsHappened.append("x evaluated")
        "blah"
      }
    }
    assert(a.b.x === null)
    assert(a.c.b.x === null)
    assert(whenThingsHappened === Seq("b created", "c created", "x evaluated"))
  }

  test("when something concrete depends on something abstract, make the concrete thing lazy") {
    val whenThingsHappened = new ArrayBuffer[String]()
    trait A {
      val x: String
      lazy val b = new B(x)
      lazy val c = new C(b)
    }
    class B(val x: String) {
      whenThingsHappened.append("b created")
    }
    class C(val b: B) {
      whenThingsHappened.append("c created")
    }
    val a = new A {
      override val x: String = {
        whenThingsHappened.append("x evaluated")
        "blah"
      }
    }
    assert(a.b.x === "blah")
    assert(a.c.b.x === "blah")
    assert(whenThingsHappened === Seq("x evaluated", "b created", "c created"))
  }
}

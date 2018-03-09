package com.cj.scala.fundamentals

import org.scalatest.FunSuite

class FunctionCompositionSuite extends FunSuite {
  test("partially apply functions") {
    // here is a method
    def makeNumberMethod(hundreds: Int, tens: Int, ones: Int): Int = hundreds * 100 + tens * 10 + ones

    // here is that same method defined as a function value
    val makeNumberFunctionA: (Int, Int, Int) => Int = (hundreds, tens, ones) => hundreds * 100 + tens * 10 + ones

    // here are three different ways to convert the method to a function value
    val makeNumberFunctionB = makeNumberMethod(_: Int, _: Int, _: Int)
    val makeNumberFunctionC = makeNumberMethod(_, _, _)
    val makeNumberFunctionD = makeNumberMethod _

    // function values all have the apply method
    assert(makeNumberFunctionD.apply(1, 2, 3) === 123)

    // any time you don't specify the method name, "apply" is assumed
    assert(makeNumberFunctionD(1, 2, 3) === 123)

    // we can specify some values, resulting in a function with less parameters
    val makeNumberWithFiveInTensPlace = makeNumberMethod(_: Int, 5, _: Int)

    assert(makeNumberWithFiveInTensPlace(1, 2) === 152)
  }

  test("partially defined functions") {
    val less10: PartialFunction[Int, String] = {
      case x if x < 10 => "less 10: " + x
    }
    val less20: PartialFunction[Int, String] = {
      case x if x < 20 => "less 20: " + x
    }
    val less30: PartialFunction[Int, String] = {
      case x if x < 30 => "less 30: " + x
    }
    val notPartial = (x: Int) => "big: " + x
    val big: PartialFunction[Int, String] = {
      case x => notPartial(x)
    }
    val composed = less10 orElse less20 orElse less30 orElse big

    assert(composed(5) === "less 10: 5")
    assert(composed(15) === "less 20: 15")
    assert(composed(25) === "less 30: 25")
    assert(composed(35) === "big: 35")
  }

  test("you can use a partially defined function to operate on map entries") {
    val theMap = Map(1 -> 2, 3 -> 4)
    val expectedKeys = Set(1, 3)

    def getKey(entry: (Int, Int)): Int = {
      val (key, value) = entry
      key
    }

    assert(theMap.map(getKey).toSet === expectedKeys)

    def getKeyMethod(key: Int, value: Int): Int = {
      key
    }

    val getKeyFunction = getKeyMethod _
    val getKeyTupled = getKeyFunction.tupled
    assert(theMap.map(getKeyTupled).toSet === expectedKeys)

    assert(theMap.map { case (key, value) => key }.toSet === expectedKeys)
  }

  test("get tupled versions of functions") {
    def foo(x: Int, y: String) = (y, x)

    val bar: (Int, String) => (String, Int) = (x, y) => (y, x)
    val fooFunction = foo _

    assert(Map(1 -> "a").map(fooFunction.tupled) === Map("a" -> 1))
    assert(Map(1 -> "a").map(bar.tupled) === Map("a" -> 1))
  }

}

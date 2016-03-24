package com.cj.scala.fundamentals

import org.scalatest.{BeforeAndAfter, FunSuite}


class FunctionSuite extends FunSuite with BeforeAndAfter {

  var testCase = (1, s".)")

  //
  //  def incTC()  : = testCase ={
  //    testCase = (testCase._1 + 1,
  //  }
  // Mutatis mutandis!

  before {
    //    incTC()
  }

  test(s"1.) functions are considered first class citizens in scala which means we can treat them just like values") {

    def flipMyInteger(i: Int) = i.toString.reverse.toInt

    assert(flipMyInteger(123) === 321)

    // this is the same thing
    val flipMyInteger_AsValue = (i: Int) => i.toString.reverse.toInt


    assert(flipMyInteger_AsValue(123) === 321)

    // this means i can pass the function around just like a value

    def flipIntTwice(fn: (Int) => Int, i: Int): Int = {
      fn(fn(i))
    }

    assert(flipIntTwice(flipMyInteger, 123) === 123)

    // furthermore we can return functions from functions 

    def returnsFlipIntTwice(fn: (Int) => Int) = {
      (i: Int) => fn(fn(i))
    }

    val returnedFn: (Int) => Int = returnsFlipIntTwice(flipMyInteger)

    assert(returnedFn(123) === 123)
  }


  test("2.) functions may be defined with multiple parameter lists") {

    def makeTheSalad(a: Apple)(b: Banana)(c: Cantaloupe) =
      new FruitSalad(List(a, b, c))


    val allFruit = makeTheSalad(new Apple)(new Banana)(new Cantaloupe)
    assert(allFruit.blend === "smooth,appealing,rough")

    //we may even `partially` apply the arguments
    val onlyAnApple: (Banana) => (Cantaloupe) => FruitSalad = makeTheSalad(new Apple)
    val withBanana: (Cantaloupe) => FruitSalad = onlyAnApple(new Banana)
    val withCantaloupeMakesFruitSalad = withBanana(new Cantaloupe)
    val blended = withCantaloupeMakesFruitSalad
    assert(blended.blend === "smooth,appealing,rough")

    //we may even partially apply the arguments without type signatures but we need to use the underscore
    val onlyAnApple_2 = makeTheSalad(new Apple) _ // <<<=== here `_` is !
    val withBanana_2 = onlyAnApple_2(new Banana)
    val withCantaloupeMakesFruitSalad_2 = withBanana_2(new Cantaloupe)
    assert(withCantaloupeMakesFruitSalad_2.blend === "smooth,appealing,rough")

  }

  test("3.) having multiple parameter lists is reminiscent of traditional `currying`") {

    def curry[A, B, C](f: (A, B) => C): A => (B => C) = a => b => f(a, b)

    def summa(i: Int, ii: Int) = s"Tomas Aquino is ${i + ii} times smarter than all of us"

    val schoenfinkeled: (Int) => (Int) => String = curry(summa) // Mad-Props to the Professa!

    assert(schoenfinkeled(10)(3) === "Tomas Aquino is 13 times smarter than all of us")

  }

  test("4.) partial function application means you can supply args as they are available to you ") {

    def concats(a: Int, b: String, c: Double) =
      s"a=>$a b=>$b c=>$c"

    assert(concats(1, "2", 3.0) === "a=>1 b=>2 c=>3.0")

    val needInt: (Int) => String = concats(_, "2", 3.0)
    assert(needInt(1) === "a=>1 b=>2 c=>3.0")

    val needString: (String) => String = concats(1, _, 3.0)
    assert(needString("2") === "a=>1 b=>2 c=>3.0")

    val needDouble: (Double) => String = concats(1, "2", _)
    assert(needDouble(3.0) === "a=>1 b=>2 c=>3.0")

    val needsTheOuterParams: (Int, Double) => String = concats(_, "2", _)
    assert(needsTheOuterParams(1, 3.0) === "a=>1 b=>2 c=>3.0")

    val allThree = concats(_, _, _) // which the same as concats _
    assert(allThree(1, "2", 3.0) === "a=>1 b=>2 c=>3.0")

    // without explicit val typing the compiler needs a little help inferring the partially applied type
    val needInt_explicit = concats(_: Int, "2", 3.0)
    assert(needInt_explicit(1) === "a=>1 b=>2 c=>3.0")


    /*
      partial function application helps us 'fuse' functions that otherwise are incompatible
     */


    def putsItAllTogether(article: Char, thing: String, number: Double) =
      s"I AM $article $thing version: $number"

    def neo(fn: String => String) = fn("Human")

    val theMatrix = putsItAllTogether('A', _: String, 2.0: Double)

    assert(neo(theMatrix) === "I AM A Human version: 2.0")
  }


  //  inc()

  test(s"5.) composition and andThen") {

    def square(x: Int) = x * x
    def upAMagnitude(n: Int) = Math.pow(n.toDouble, 10.0)

    val oldWay = {
      val squared: Int = square(2)
      val result = upAMagnitude(squared)
      result
    }
    assert(oldWay == 1048576.0)

    /**
      *
      * Function composition is nifty shorthand that treats functions as lego-blocks stuck together.
      * Using composition we avoid deeply nested functional expressions and more naturally describe our
      * problem and a series of linear steps to reach our goal.
      *
      * As long as return signature of one function matched the input signature of another you can
      * `compose` functions together yielding a new function.
      *
      * Traditionally speaking Function composition in F.P. is symbolized by the 'circle-dot' operator => ဝ
      *
      * Then when we say `f compose g` we write this as `f ဝ g`
      * which evaluates to the equivalent statement => f( g (x) )
      *
      * The real benefit in composition lies in finding new combinations of functional `bricks`
      * that `just go together` like chocolate and peanut butter
      *
      * And-Then works in a more `natural feeling` left to right ordered function pipeline
      *
      * We say g andThen f =yields=> g and then apply f, which again is the equivalent of f(g(x))
      *
      *
      */

    val composed = (upAMagnitude _).compose(square) //  f ဝ g  =is-same-as=> f(g(x))
    assert(composed(2) == 1048576.0)

    val andThens = (square _).andThen(upAMagnitude) // (g andThen f) =-s-same-as=> f(g(x))
    assert(andThens(2) == 1048576.0)


    val lotsaFnsComposed = andThens.andThen(_.toString).andThen(_.reverse).andThen(_.toFloat).andThen(_ / 2)

    assert(lotsaFnsComposed(2) == "1048576.0".reverse.toFloat / 2)

    /**
      * so what's the benefit? lazyiness
      *
      * '"1048576.0".reverse.toFloat / 2' is a complete expression which is eagerly evaluated in place
      * yielding its result
      *
      * lotsaFnsComposed is just another value that happens to be a function
      * We can pass it around to callers or even augment with additional functions!
      * We can even decide to throw it away and not call it at all if that is an appropriate action
      * We are just steadily building a data-structure of 'potential functionality' to be applied at will
      * Or not at all. Whenever we want as many times as we want.
      */

    /**
      * remember this from above? let's try a little composition - enhancing the existing function.
      */

    def concats(a: Int, b: String, c: Double) =
      s"a=>$a b=>$b c=>$c"

    def iTakeStringsOnly(fn: String => String) = fn("I take `strings`")

    val interleaveAStringGivenOtherArgs = concats(1, _: String, 3.0: Double)

    val result = iTakeStringsOnly(interleaveAStringGivenOtherArgs)

    assert(result === "a=>1 b=>I take `strings` c=>3.0")

    val wacky: ((String) => String) => String = (iTakeStringsOnly _).andThen(interleaveAStringGivenOtherArgs)

    assert(wacky(s => s"$s extra text appended") === "a=>1 b=>I take `strings` extra text appended c=>3.0")
  }

}


trait Fruit {
  def skin: String
}

class Apple extends Fruit {
  override def skin: String = "smooth"
}

class Banana extends Fruit {
  override def skin: String = "appealing"
}

class Cantaloupe extends Fruit {
  override def skin: String = "rough"
}

case class FruitSalad(fruits: List[Fruit]) {
  def blend = fruits.map {
    _.skin
  }.mkString(",")
}

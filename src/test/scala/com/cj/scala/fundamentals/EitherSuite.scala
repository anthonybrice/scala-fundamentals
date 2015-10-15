package com.cj.scala.fundamentals

import org.scalatest.FunSuite

class EitherSuite extends FunSuite {
  //a common use for Either is validation, putting a valid result on one alternative, an invalid result in the other
  //since "right" in a different context can mean "correct", it was observed that this can be used as a mnemonic
  //this has led to a convention of putting the valid answer on the right, and the invalid answer on the left
  //remember, the "right" side of the either contains the "right" (correct) answer
  test("stop on first validation error") {
    //lets validate a bunch of samples, ensuring we get the correct messages each time
    val sampleInputs = Seq(null, "", "aaa", "-1", "234", "50")
    val actual = sampleInputs.map(ValidationRules.toValidQuantity)
    val expected: Seq[Either[String, Int]] = Seq(
      Left("must not be null"),
      Left("must not be blank"),
      Left("must be a number, was 'aaa'"),
      Left("must be at least 1, was -1"),
      Left("must be at most 100, was 234"),
      Right(50)) //here the validation was successful, so we have the "right" answer, statically typed
    assert(actual === expected)
  }

  //An example of how you might validate if you needed a list of all the validation errors
  //The rules are as follows:
  //name
  //  must not be null
  //  must not contain whitespace
  //shape
  //  must be one of Triangle, Circle, Square
  //quality
  //  must not be null
  //  must not be blank
  //  must be a number
  //  must be at least 1
  //  must be at most 100
  test("accumulate validation errors") {
    //when everything is valid, we should get the statically typed value
    assert(
      Part.fromStringValues(Map("name" -> "bit", "shape" -> "triangle", "quality" -> "79")) ===
        Right(Part("bit", Shape.Triangle, 79)))
    //if there are validation errors, we should get the list of validation messages
    assert(
      Part.fromStringValues(Map("name" -> "bit and a bob", "shape" -> "trapezoid", "quality" -> "wat")) ===
        Left(Map(
          "name" -> "must not contain whitespace, was 'bit and a bob'",
          "shape" -> "was 'trapezoid', expected one of Triangle, Circle, Square",
          "quality" -> "must be a number, was 'wat'")))
  }

  test("curried constructor") {
    val a = (Part.apply _).curried
    val b = a("bit")
    val c = b(Shape.Triangle)
    val d = c(79)
    assert(d === Part("bit", Shape.Triangle, 79))
  }

  test("compose valid from curried") {
    val validName: Either[(String, String), String] = Right("bit")
    val validShape: Either[(String, String), Shape] = Right(Shape.Triangle)
    val validQuality: Either[(String, String), Long] = Right(79)

    val curriedFunction: (String) => (Shape) => (Long) => Part = (Part.apply _).curried
    val a = applyFirst(curriedFunction, validName)
    val b = applyNext(a, validShape)
    val c = applyNext(b, validQuality)
    assert(c === Right(Part("bit", Shape.Triangle, 79)))
  }

  test("compose invalid from curried") {
    val validName: Either[(String, String), String] = Left("name" -> "must not contain whitespace, was 'bit and a bob'")
    val validShape: Either[(String, String), Shape] = Left("shape" -> "was 'trapezoid', expected one of Triangle, Circle, Square")
    val validQuality: Either[(String, String), Long] = Left("quality" -> "must be a number, was 'wat'")

    val curriedFunction: (String) => (Shape) => (Long) => Part = (Part.apply _).curried
    val a = applyFirst(curriedFunction, validName)
    val b = applyNext(a, validShape)
    val c = applyNext(b, validQuality)
    assert(c === Left(Map(
      "name" -> "must not contain whitespace, was 'bit and a bob'",
      "shape" -> "was 'trapezoid', expected one of Triangle, Circle, Square",
      "quality" -> "must be a number, was 'wat'")))
  }

  def applyFirst[T, R](f: T => R,
                       a: Either[(String, String), T]): Either[Map[String, String], R] = {
    val result: Either[Map[String, String], R] = a match {
      case Left(newError) => Left(Map() + newError)
      case Right(newValue) => Right(f(newValue))
    }
    result
  }

  def applyNext[T, R](soFar: Either[Map[String, String], T => R],
                      a: Either[(String, String), T]): Either[Map[String, String], R] = {
    val result: Either[Map[String, String], R] = (soFar, a) match {
      case (Left(existingErrors), Left(newError)) => Left(existingErrors + newError)
      case (Left(existingErrors), Right(_)) => Left(existingErrors)
      case (Right(f), _) => applyFirst(f, a)
    }
    result
  }

  object ValidationRules {
    def disallowNull(input: String): Either[String, String] = {
      if (input == null) Left("must not be null")
      else Right(input)
    }

    def disallowBlank(input: String): Either[String, String] = {
      if (input.trim == "") Left("must not be blank")
      else Right(input)
    }

    def disallowWhitespace(input: String): Either[String, String] = {
      if (input.matches( """.*\s.*""")) Left(s"must not contain whitespace, was '$input'")
      else Right(input)
    }

    def requireNumber(input: String): Either[String, Int] = {
      try {
        Right(input.toInt)
      } catch {
        case _: NumberFormatException => Left(s"must be a number, was '$input'")
      }
    }

    //this validation rule has parameters
    //so rather than doing the validation
    //create a function that does the validation with the proper parameters
    def requireAtLeast(atLeast: Int): Int => Either[String, Int] =
      (input: Int) => {
        if (input < atLeast) Left(s"must be at least $atLeast, was $input")
        else Right(input)
      }

    def requireAtMost(atMost: Int): Int => Either[String, Int] =
      (input: Int) => {
        if (input > atMost) Left(s"must be at most $atMost, was $input")
        else Right(input)
      }

    def requireAtLeast1 = requireAtLeast(1)

    def requireAtMost100 = requireAtMost(100)

    //compose a bunch of rules in order, stop on the first failure
    def toValidQuantity(input: String): Either[String, Long] = {
      //if we only need to record the first failure, we can compose along the "right" projection
      //unlike options, it is not obvious if we want to follow the left path or right path
      //so we have to specify it in the for comprehension
      //note that if we project right on a left, nothing happens
      //if we project right on a right, the statically typed value is carried over
      for {
        a <- disallowNull(input).right
        b <- disallowBlank(a).right
        c <- requireNumber(b).right
        d <- requireAtLeast1(c).right
        e <- requireAtMost100(d).right
      } yield {
        e
      }
    }

    def toValidName(input: String): Either[String, String] = {
      for {
        a <- disallowNull(input).right
        b <- disallowWhitespace(a).right
      } yield {
        b
      }
    }
  }

  case class Part(name: String, shape: Shape, quality: Long)

  object Part {

    import ValidationRules._

    def fromStringValues(map: Map[String, String]) = {
      def applyRule[T](name: String, rule: String => Either[String, T]): Either[(String, String), T] = {
        for {
          a <- rule(map(name)).left
        } yield {
          (name, a)
        }
      }
      val errorOrName = applyRule("name", toValidName)
      val errorOrShape = applyRule("shape", Shape.eitherFromString)
      val errorOrQuality = applyRule("quality", toValidQuantity)
      val curriedPartConstructor = (Part.apply _).curried
      val a = applyFirst(curriedPartConstructor, errorOrName)
      val b = applyNext(a, errorOrShape)
      val c = applyNext(b, errorOrQuality)
      c
    }
  }

}

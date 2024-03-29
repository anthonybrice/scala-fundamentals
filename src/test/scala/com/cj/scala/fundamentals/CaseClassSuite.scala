package com.cj.scala.fundamentals

import org.scalatest.FunSuite

class CaseClassSuite extends FunSuite {
  test("built in methods") {
    case class Point(x: Int, y: Int) //"val" is automatically inferred for x and y because this is a case class

    val a = new Point(1, 2) //the regular constructor you would get in a non-case class
    val b = Point.apply(1, 2) //A factory method is generated in the companion object
    val c = Point(1, 2) //when you omit the method name, "apply" is assumed
    val d = a.toString //we get a sensible toString
    val e = a == b //the equals operator is value equality, not reference equality
    val f = Point(3, 4)
    val g = a == f
    val h = a.copy(x = 5) //we also have copy constructors, which are very useful when combined with named parameters
    val i = a.copy(y = 6)

    assert(a === Point(1, 2))
    assert(b === Point(1, 2))
    assert(c === Point(1, 2))
    assert(d === "Point(1,2)")
    assert(e === true)
    assert(f === Point(3, 4))
    assert(g === false)
    assert(h === Point(5, 2))
    assert(i === Point(1, 6))
  }

  test("extractors") {

    case class Point(x: Int, y: Int) //the parameter list here serves as the specification for the primary constructor

    //these three lines do the same thing, prefer the last one, the verbose versions are here to let you know how it works
    val a = new Point(1, 2) //create a point using the constructor
    val b = Point.apply(1, 2) //or use the factory method generated by the case class
    val c = Point(1, 2) //as always, if the method name is omitted, .apply() is assumed

    val Point(d, e) = c //While .apply() puts an object together from its constructor, the extractor pulls an object apart, using the same ordering as the primary constructor

    assert(a === Point(1, 2))
    assert(b === Point(1, 2))
    assert(c === Point(1, 2))
    assert(d === 1)
    assert(e === 2)

  }
  test("nested extractor") {

    case class Point(x: Int, y: Int)

    case class Rectangle(topLeft: Point, bottomRight: Point)

    val a = Rectangle(Point(1, 2), Point(3, 4))
    val Rectangle(Point(b, c), d) = a //you can use extractors within extractors
    val Rectangle(_, Point(e, _)) = a //use underscores to indicate elements you don't care about

    assert(a === Rectangle(Point(1, 2), Point(3, 4)))
    assert(b === 1)
    assert(c === 2)
    assert(d === Point(3, 4))
    assert(e === 3)
  }

  test("pattern match") {
    case class Coordinate(x: Int, y: Int)

    val baseline = Coordinate(1, 2)
    val sameAsBaseline = Coordinate(1, 2)
    val xDifferent = Coordinate(3, 2)
    val yDifferent = Coordinate(1, 4)
    val yDifferentCopy = yDifferent.copy(x = 5)

    //notice that pattern matching does not 'fall through' as is the case with switch statements in Java
    def describeCoordinate(coordinate: Coordinate): String = coordinate match {
      case Coordinate(1, 2) => "looks like baseline" //match the values exactly
      case Coordinate(1, _) => "starts with 1" //match x to 1, don't care what y is
      case Coordinate(x, 2) => s"ends with 2, x is $x" //match y to 2, recover the value for x
      case Coordinate(x, y) => s"another coordinate: values: x = $x, y = $y" //get the values for x and y
    }

    assert(describeCoordinate(baseline) === "looks like baseline")
    assert(describeCoordinate(sameAsBaseline) === "looks like baseline")
    assert(describeCoordinate(xDifferent) === "ends with 2, x is 3")
    assert(describeCoordinate(yDifferent) === "starts with 1")
    assert(describeCoordinate(yDifferentCopy) === "another coordinate: values: x = 5, y = 4")
  }
}

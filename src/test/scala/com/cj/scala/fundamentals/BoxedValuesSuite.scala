package com.cj.scala.fundamentals

import java.lang.{Integer => BoxedInt, Long => BoxedLong}
import java.util

import org.scalatest.FunSuite

import scala.collection.JavaConverters._

class BoxedValuesSuite extends FunSuite {
  def javaStyleReverse(values: Array[Long]): Array[Long] = {
    val boxedValues: util.List[BoxedLong] = new util.ArrayList[BoxedLong]
    for (value <- values) {
      boxedValues.add(value)
    }
    util.Collections.reverse(boxedValues)
    val reversedValues: Array[Long] = new Array[Long](values.length)
    for {i <- values.indices} {
      reversedValues(i) = boxedValues.get(i)
    }
    reversedValues
  }

  def javaStyleReverse(values: util.Collection[BoxedLong]): util.Collection[BoxedLong] = {
    val list: util.List[BoxedLong] = new util.ArrayList[BoxedLong](values)
    util.Collections.reverse(list)
    list
  }

  test("arrays of primitives") {
    val values: Seq[Long] = Seq(1, 2, 3)

    val reversedUsingArray: Seq[Long] = javaStyleReverse(values.toArray)
    assert(reversedUsingArray === Seq(3, 2, 1))
  }

  //It is important to understand all of the java converters before choosing one
  //In particular, you need to know which ones return the original object when converting back and forth
  //Read the documentation here
  //http://www.scala-lang.org/api/current/scala/collection/JavaConverters$.html
  test("java collections") {
    val values: Seq[Long] = Seq(1, 2, 3)

    //use valueOf to box
    val boxed: Seq[BoxedLong] = values.map(BoxedLong.valueOf)
    val collection: util.Collection[BoxedLong] = boxed.asJava
    val reversedCollection: util.Collection[BoxedLong] = javaStyleReverse(collection)
    val reversedBoxed: Seq[BoxedLong] = reversedCollection.asScala.toSeq
    //use longValue to unbox
    val reversedUsingCollection: Seq[Long] = reversedBoxed.map(_.longValue())
    assert(reversedUsingCollection === Seq(3, 2, 1))

    //same as above, only in one statement
    val reversedUsingCollectionOneStatement: Seq[Long] =
    javaStyleReverse(values.map(BoxedLong.valueOf).asJava).asScala.map(_.longValue()).toSeq
    assert(reversedUsingCollectionOneStatement === Seq(3, 2, 1))
  }

  test("get tupled versions of functions") {
    def foo(x: Int, y: String) = (y, x)
    val bar: (Int, String) => (String, Int) = (x, y) => (y, x)
    val fooFunction = foo _

    assert(Map(1 -> "a").map(fooFunction.tupled) === Map("a" -> 1))
    assert(Map(1 -> "a").map(bar.tupled) === Map("a" -> 1))
  }

  test("mutable to immutable map") {
    val mutable = scala.collection.mutable.Map(1 -> 2, 3 -> 4)
    val immutable: scala.collection.immutable.Map[Int, Int] = mutable.toSeq.toMap
    assert(immutable === Map(1 -> 2, 3 -> 4))
  }

  test("interop with more complex collections") {
    val toJavaD: (String, Int) => (String, BoxedInt) = (key, value) => (key, Integer.valueOf(value))
    val toJavaC: Map[String, Int] => util.Map[String, BoxedInt] = value => value.map(toJavaD.tupled).asJava
    val toJavaB: (Long, Map[String, Int]) => (BoxedLong, util.Map[String, BoxedInt]) = (key, value) => (BoxedLong.valueOf(key), toJavaC(value))
    val toJavaA: Map[Long, Map[String, Int]] => util.Map[BoxedLong, util.Map[String, BoxedInt]] = value => value.map(toJavaB.tupled).asJava

    val toScalaC: (String, BoxedInt) => (String, Int) = (key, value) => (key, value.intValue())
    val toScalaB: (BoxedLong, util.Map[String, BoxedInt]) => (Long, Map[String, Int]) = (key, value) => (key.intValue(), value.asScala.toSeq.map(toScalaC.tupled).toMap)
    val toScalaA: util.Map[BoxedLong, util.Map[String, BoxedInt]] => Map[Long, Map[String, Int]] = value => value.asScala.toSeq.map(toScalaB.tupled).toMap

    val originalInScala: Map[Long, Map[String, Int]] = Map(2L -> Map("a" -> 3))
    val convertedToJava: util.Map[BoxedLong, util.Map[String, BoxedInt]] = toJavaA(originalInScala)
    val convertedBackToScala = toScalaA(convertedToJava)

    assert(originalInScala === convertedBackToScala)
  }
}

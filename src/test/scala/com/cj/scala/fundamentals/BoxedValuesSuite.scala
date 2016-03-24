package com.cj.scala.fundamentals

import java.lang.{Long => BoxedLong}
import java.util

import org.scalatest.FunSuite

import scala.collection.JavaConversions

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

  test("java collections") {
    val values: Seq[Long] = Seq(1, 2, 3)

    val boxed: Seq[BoxedLong] = values.map(BoxedLong.valueOf)
    val collection: util.Collection[BoxedLong] = JavaConversions.asJavaCollection(boxed)
    val reversedCollection: util.Collection[BoxedLong] = javaStyleReverse(collection)
    val reversedBoxed: Seq[BoxedLong] = JavaConversions.iterableAsScalaIterable(reversedCollection).toSeq
    val reversedUsingCollection: Seq[Long] = reversedBoxed.map(_.longValue())
    assert(reversedUsingCollection === Seq(3, 2, 1))

    val reversedUsingCollectionOneLine: Seq[Long] =
      JavaConversions.iterableAsScalaIterable(javaStyleReverse(JavaConversions.asJavaCollection(values.map(BoxedLong.valueOf)))).map(_.longValue()).toSeq
    assert(reversedUsingCollectionOneLine === Seq(3, 2, 1))
  }
}
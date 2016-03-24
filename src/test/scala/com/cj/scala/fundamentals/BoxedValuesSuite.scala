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

  //It is important to understand all of the java conversions before choosing one
  //In particular, you need to know which once return the original object when converting back and forth
  //Read the documentation here
  //http://www.scala-lang.org/api/current/#scala.collection.JavaConversions$
  test("java collections") {
    val values: Seq[Long] = Seq(1, 2, 3)

    //use valueOf to box
    val boxed: Seq[BoxedLong] = values.map(BoxedLong.valueOf)
    val collection: util.Collection[BoxedLong] = JavaConversions.asJavaCollection(boxed)
    val reversedCollection: util.Collection[BoxedLong] = javaStyleReverse(collection)
    val reversedBoxed: Seq[BoxedLong] = JavaConversions.iterableAsScalaIterable(reversedCollection).toSeq
    //use longValue to unbox
    val reversedUsingCollection: Seq[Long] = reversedBoxed.map(_.longValue())
    assert(reversedUsingCollection === Seq(3, 2, 1))

    //same as above, only in one line
    val reversedUsingCollectionOneLine: Seq[Long] =
      JavaConversions.iterableAsScalaIterable(javaStyleReverse(JavaConversions.asJavaCollection(values.map(BoxedLong.valueOf)))).map(_.longValue()).toSeq
    assert(reversedUsingCollectionOneLine === Seq(3, 2, 1))
  }
}

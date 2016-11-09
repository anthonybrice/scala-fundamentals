Getting started with Scala
===

Catch up with our standards
===
[scala standards](http://gitlab.cj.com/cjdev/standards/blob/master/scala-standards.md)

Look at this example with full test coverage to see how wiring works
====================================================================
* what belongs in wiring
    * constructor injection
    * new'ing instances of objects
    * constant values
    * function references
    * partial function application
    * explicit typing
* what should not be allowed in wiring
    * type inference 
    * conditional logic
    * nulls
* designing classes for use with wiring
    * one entry point
        * no logic is allowed above the wiring
        * the one entry point does nothing but delegate to the wiring
        * this ensures that all of your logic is easy to unit test
    * constructors should not do much
        * one class should not depend on constructor code from another class in the same wiring
        * this creates a temporal coupling that is not obvious
        * in general, constructors should do nothing but initialize references to their collaborators
* key features
    * one [entry point](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/server/src/main/scala/com/cj/latestdeployable/server/ServerApplication.scala), which allowed us to find dead code with detangler
    * full test coverage of logic and boundaries
    * tests are giving us earliest possible feedback
        * instant, if you are looking at the file, by static typing and fully qualifying types in [wiring](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/server/src/main/scala/com/cj/latestdeployable/server/ServerWiring.scala)
        * compile time, by static typing and fully qualifying types in [wiring](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/server/src/main/scala/com/cj/latestdeployable/server/ServerWiring.scala)
        * [logic test](http://devops102.wl.cj.com:12300/types-of-tests.html), by design by contract and dependency inversion
        * [boundary test](http://devops102.wl.cj.com:12300/types-of-tests.html), only the minimal needed to ensure the integration point with the boundary works, no conditional logic
            * [sample boundary test](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/core/src/test/scala/com/cj/latestdeployable/core/GetViaHttpTest.scala)
            * [fixture for boundary test](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/core/src/test/scala/com/cj/latestdeployable/core/HttpServerApp.scala)
            * [hello handler](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/core/src/test/scala/com/cj/latestdeployable/core/HelloHandler.scala)
    * some features of the wiring itself
        * statically typed
        * happens at compile time
        * instant feedback from integrated development environment
        * no annotations
        * no proxies clogging up stack trace
        * can set breakpoints
        * no re-wiring upon each launch of a test

Value Simplicity
===
* Meet Customer Need
* Easy To Test
* Clearly Express Intent
* No Duplicate Code
* Concise As Possible

Understand the Fundamentals
===
Make sure you understand some fundamentals of Scala

* [tuples and extractors](src/test/scala/com/cj/scala/fundamentals/TuplesAndExtractorsSuite.scala)
* [options](src/test/scala/com/cj/scala/fundamentals/OptionSuite.scala)
* [case classes](src/test/scala/com/cj/scala/fundamentals/CaseClassSuite.scala)
* [immutable collections](src/test/scala/com/cj/scala/fundamentals/CollectionSuite.scala)
* [map, flatMap, for/yield](src/test/scala/com/cj/scala/fundamentals/MapFlatMapForYieldSuite.scala)
* [regular expressions](src/test/scala/com/cj/scala/fundamentals/RegexExtractorSuite.scala)
* [composing immutable collections](src/test/scala/com/cj/scala/fundamentals/TypesOfLoopsSuite.scala)
* [futures](src/test/scala/com/cj/scala/fundamentals/FutureSuite.scala)
* [either](src/test/scala/com/cj/scala/fundamentals/EitherSuite.scala)
* [for comprehensions, map, and flatMap](src/test/scala/com/cj/scala/fundamentals/OptionMapFlatMapForYieldSuite.scala)
* [java interoperation, boxing](src/test/scala/com/cj/scala/fundamentals/BoxedValuesSuite.scala)
* sorting case classes [test](src/test/scala/com/cj/scala/fundamentals/SortCaseClassSuite.scala)[implementation](src/test/scala/com/cj/scala/fundamentals/SortMe.scala)

Scala List is not Java List
===
Remember that the Scala List is a singly linked list.

* If all you need is ordered values, use a Seq instead, no need to over-specify the implementation.
* If you need efficient random access, use IndexedSeq instead, as this will guarantee you an efficient random access implementation (such as Vector).
* If you are taking advantage of being able to access the head and tail really fast (common with recursive algorithms), use List.

Constants don't need all caps
===
Don't all-caps something just because it is a val.  It made sense in Java because in Java something being constant is not the default, but in Scala it is easy to make most of your code immutable.  If you capitalized every "val" in Scala, the majority of your code would be all caps.


Line breaks can affect meaning
===
Break lines so it is obvious in the preceding line that another operation is expected, for example
instead of

    val x = 1 + 2
          + 3 + 4

do

    val x = 1 + 2 +
            3 + 4

This is necessary because the first situation confuses semicolon inference, as both lines are legal on their own, but they were meant to be combined.

Understand your evaluation options
===
* val
    * computed only once, immediately upon declaration, immutable
* lazy val
    * computed only once, when first accessed, immutable
* var
    * computed immediately upon declaration, mutable
* def
    * computed every time it is accessed
* by value parameters (theParameter: String)
    * computed exactly once, just before the function is invoked
* by name parameters (theParameter: => String)
    * computed only when referenced, and re-computed every time it is referenced

Understand your collection options
===
* Traversable        - can be iterated over all at once, gives you the vast majority of collection operations by only implementing one function (foreach)
* Iterable           - can be iterated over one at a time
    * Seq            - ordered sequence
        * IndexedSeq - performs well with random access
        * List       - performs well with head, tail, isEmpty (ideal for many recursive algorithms)
        * Range      - expresses a range of values in terms of start, end, and step
        * Stream     - a lazy sequence, computes all elements up to and including the one you ask for only when you ask for it, and does not recompute already computed values when you ask for more values
    * Set            - unique values
    * Map            - key value pairs, can also be thought of as an unordered Seq of tuple2's with unique keys
        * ListMap    - preserves insertion order at the cost of access being linear time rather than constant time

JavaConverters
==============
When interacting with Java code, understand how to switch between Scala and Java collections

* scala.collection.JavaConverters provides both two-way and one-way conversions, make sure you understand them
* Remember that Scala obviates the need for boxing because it treats primitives the same way as any other object.  A consequence of this is that when you are converting to/from Java collections, you will have to code any necessary boxing/unboxing yourself.  You may find import aliasing useful when doing this.

        import java.lang.{Long=>BoxedLong}.

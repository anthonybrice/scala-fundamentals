# Getting started with Scala

## Understand the Fundamentals

### Make sure you understand some fundamentals of Scala

- [tuples and extractors](src/test/scala/com/cj/scala/fundamentals/TuplesAndExtractorsSuite.scala)
- [case classes](src/test/scala/com/cj/scala/fundamentals/CaseClassSuite.scala)
- [options](src/test/scala/com/cj/scala/fundamentals/OptionSuite.scala)
- [immutable collections](src/test/scala/com/cj/scala/fundamentals/CollectionSuite.scala)
- [map, flatMap, for/yield](src/test/scala/com/cj/scala/fundamentals/MapFlatMapForYieldSuite.scala)
- [regular expressions](src/test/scala/com/cj/scala/fundamentals/RegexExtractorSuite.scala)
- [types of loops](src/test/scala/com/cj/scala/fundamentals/TypesOfLoopsSuite.scala)

### Monads
Scala supports the "Monad" design pattern.
The concept can be very difficult to understand the first time you are exposed to it,
so initially it is sufficient to think of a monad as anything with a .flatMap method
and Scala's "for loop" as a monad runner,
where "<-" pulls the monadic value out of the monad.

Here are some resources to explore Monads in more detail if you are interested.    
- [Learning about Monads](https://byorgey.wordpress.com/2009/01/12/abstraction-intuition-and-the-monad-tutorial-fallacy/)
- [Visual exclamation in Haskell](http://adit.io/posts/2013-04-17-functors,_applicatives,_and_monads_in_pictures.html)
- [Example based exclamation in Kotlin](https://arrow-kt.io/docs/patterns/monads/)

### Some more advanced stuff
- [futures](src/test/scala/com/cj/scala/fundamentals/FutureSuite.scala)
- [function composition](src/test/scala/com/cj/scala/fundamentals/FunctionCompositionSuite.scala)
- [either](src/test/scala/com/cj/scala/fundamentals/EitherSuite.scala)
- [for comprehensions, map, and flatMap](src/test/scala/com/cj/scala/fundamentals/OptionMapFlatMapForYieldSuite.scala)
- [java interoperation, boxing](src/test/scala/com/cj/scala/fundamentals/BoxedValuesSuite.scala)
- sorting case classes: [test](src/test/scala/com/cj/scala/fundamentals/SortCaseClassSuite.scala), [implementation](src/test/scala/com/cj/scala/fundamentals/SortMe.scala)
- [why to use lazy when injecting dependencies](src/test/scala/com/cj/scala/fundamentals/WhyToUseLazyWhenInjectingDependencies.scala)
- [concurrency models](http://gitlab.cj.com/cjdev/onboarding-concurrency-models)

### CJ Scala Standards
- [CJ Scala Standards](http://gitlab.cj.com/cjdev/old-standards-see-engineering-commitments/blob/master/scala-standards.md)

Look at this [example](http://gitlab.cj.com/cjdev/latest-deployable/) with full test coverage to see how dependency injection works
====================================================================
- what belongs in dependency injection
    - constructor injection
    - new'ing instances of objects
    - constant values
    - function references
    - partial function application
    - explicit typing
- what should not be allowed in dependency injection
    - type inference 
    - conditional logic
    - nulls
- designing classes for use with dependency injection
    - one entry point
        - no logic is allowed above the dependency injection
        - the one entry point does nothing but delegate to the dependency injection
        - this ensures that all of your logic is easy to unit test
    - constructors should not do much
        - one class should not depend on constructor code from another class in the same dependency injection
        - this creates a temporal coupling that is not obvious
        - in general, constructors should do nothing but initialize references to their collaborators
- key features
    - one [entry point](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/server/src/main/scala/com/cj/latestdeployable/server/ServerApplication.scala), which allowed us to find dead code with detangler
    - full test coverage of logic and boundaries
    - tests are giving us earliest possible feedback
        - instant, if you are looking at the file, by static typing and fully qualifying types in [dependency injection](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/server/src/main/scala/com/cj/latestdeployable/server/ServerDependencyInjection.scala)
        - compile time, by static typing and fully qualifying types in [dependency injection](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/server/src/main/scala/com/cj/latestdeployable/server/ServerDependencyInjection.scala)
        - [logic test](http://shell1.vclk.net/~sshubin/types-of-tests.svg), by design by contract and dependency inversion
        - [boundary test](http://shell1.vclk.net/~sshubin/types-of-tests.svg), only the minimal needed to ensure the integration point with the boundary works, no conditional logic
            - [sample boundary test](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/domain/src/test/scala/com/cj/latestdeployable/domain/GetViaHttpTest.scala)
            - [fixture for boundary test](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/domain/src/test/scala/com/cj/latestdeployable/domain/HttpServerApp.scala)
            - [hello handler](http://gitlab.cj.com/cjdev/latest-deployable/blob/master/domain/src/test/scala/com/cj/latestdeployable/domain/HelloHandler.scala)
    - some features of the dependency injection itself
        - statically typed
        - happens at compile time
        - instant feedback from integrated development environment
        - no annotations
        - no proxies clogging up stack trace
        - can set breakpoints
        - no re-injecting upon each launch of a test

Value Simplicity
===
- Meet Customer Need
- Easy To Test
- Clearly Express Intent
- No Duplicate Code
- Concise As Possible

Enumerated types are not built into the language
===
- If you don't need to iterate over all values, you can use case classes or case objects that extend a sealed trait, as shown in the [StopLight](src/test/scala/com/cj/scala/fundamentals/StopLight.scala) example.  This supports polymorphism.
- If you need to iterate over all the values and need polymorphism as well, refer to the [DateRange](src/test/scala/com/cj/scala/fundamentals/DateRange.scala) example and its corresponding [test](src/test/scala/com/cj/scala/fundamentals/EnumStyleSuite.scala)
- There is also a scala.Enumeration type for cases where you need to iterate over all the values but don't need polymorphism    

Scala List is not Java List
===
Remember that the Scala List is a singly linked list.

- If all you need is ordered values, use a Seq instead, no need to over-specify the implementation.
- If you need efficient random access, use IndexedSeq instead, as this will guarantee you an efficient random access implementation (such as Vector).
- If you are taking advantage of being able to access the head and tail really fast (common with recursive algorithms), use List.

Constants don't need all caps
===
Don't all-caps something just because it is a val.
It made sense in Java because in Java something being constant is not the default, but in Scala it is easy to make most of your code immutable.
If you capitalized every "val" in Scala, the majority of your code would be all caps.
It is okay to capitalize the first letter of a val if the intent is to indicate to the compiler that any pattern matching is to interpret the identifier as a value rather than a binding.  


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
- val
    - computed only once, immediately upon declaration, immutable
- lazy val
    - computed only once, when first accessed, immutable
- var
    - computed immediately upon declaration, mutable
- def
    - computed every time it is accessed
- by value parameters (theParameter: String)
    - computed exactly once, just before the function is invoked
- by name parameters (theParameter: => String)
    - computed only when referenced, and re-computed every time it is referenced

Understand your collection options
===
- Traversable        - can be iterated over all at once, gives you the vast majority of collection operations by only implementing one function (foreach)
- Iterable           - can be iterated over one at a time
    - Seq            - ordered sequence
        - IndexedSeq - performs well with random access
        - List       - performs well with head, tail, isEmpty (ideal for many recursive algorithms)
        - Range      - expresses a range of values in terms of start, end, and step
        - Stream     - a lazy sequence, computes all elements up to and including the one you ask for only when you ask for it, and does not recompute already computed values when you ask for more values
    - Set            - unique values
    - Map            - key value pairs, can also be thought of as an unordered Seq of tuple2's with unique keys
        - ListMap    - preserves insertion order at the cost of access being linear time rather than constant time

JavaConverters
==============
When interacting with Java code, understand how to switch between Scala and Java collections

- scala.collection.JavaConverters provides both two-way and one-way conversions, make sure you understand them
- Remember that Scala obviates the need for boxing because it treats primitives the same way as any other object.  A consequence of this is that when you are converting to/from Java collections, you will have to code any necessary boxing/unboxing yourself.  You may find import aliasing useful when doing this.

        import java.lang.{Long=>BoxedLong}.

Get detailed information about compiler warnings
================================================

    <plugin>
        <groupId>net.alchim31.maven</groupId>
        <artifactId>scala-maven-plugin</artifactId>
        <version>3.2.2</version>
        <executions>
            <execution>
                <goals>
                    <goal>compile</goal>
                    <goal>testCompile</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <args>
                <arg>-unchecked</arg>
                <arg>-deprecation</arg>
                <arg>-feature</arg>
            </args>
        </configuration>
    </plugin>

### Additional Resources
- [Tour of Scala](http://docs.scala-lang.org/tour/tour-of-scala.html)
- [Scala Exercises Tutorial](https://www.scala-exercises.org/scala_tutorial/terms_and_types)
- [Scala with Maven](http://docs.scala-lang.org/tutorials/scala-with-maven.html)
    - Note: Creating a new project via `mvn archetype:generate` renders an outdated `pom.xml` that will need updating
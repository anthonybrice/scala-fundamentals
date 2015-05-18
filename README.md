Getting started with Scala
===

Catch up with our standards
===
[scala standards](http://gitlab.cj.com/cjdev/standards/blob/master/scala-standards.md)

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
* [map, flatMap, for/yield](src/test/scala/com/cj/scala/fundamentals/MapFlatMapForYield.scala)
* [regular expressions](src/test/scala/com/cj/scala/fundamentals/RegexExtractorSuite.scala)
* [composing immutable collections](src/test/scala/com/cj/scala/fundamentals/TypesOfLoopsSuite.scala)
* [futures](src/test/scala/com/cj/scala/fundamentals/FutureSuite.scala)

No mutable case classes
===
Case classes are designed to behave like value objects in Scala.
Using them as other then values can confuse both libraries and engineers that expect case classes to be immutable.
For similar reasons, also don't inherit from case classes.
It is ok for case classes to inherit from something else as long as it is immutable.
Case classes should be leaf nodes in the inheritance hierarchy.

Favor Option over null
===
* If you need a null in an argument list or return value, use the Option type instead.  Treat null pointer exceptions in Scala as the caller's fault for sending a null, not the functions fault for not checking for null.  Sometimes you will have to deal with nulls, just don't pass them along in a parameter list.
* Having a well defined mechanic for dealing with nulls allows the code to be simpler because you know where you do and do not need null checking.  The Option type allows you to enforce this safety at compile time.
* Some Java libraries require you to use null, so when moving from Java to Scala you can use Option.apply(possiblyNullValue) to convert an option to a null.  In the other direction, you can use the option.orNull method.
* If the value is null *only* during initialization, but *never* once the application is ready, then an Option is unnecessary overhead.  In this case, it is ok to use null instead, as the after-initialization code can sensibly expect the value not to be null.
* Avoid option.get, as you are incurring the same lack of safety you get with nulls.  Instead, use option.map or pattern matching.  It is only safe to use option.get if you know the option is not None, but if you know that, why are you using an option in the first place?

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

Favor Referential transparency
===
Favor referential transparency when it makes sense.
This makes it easier to unit-test and compose your functions.
Here are some indicators that you do not have referential transparency, along with alternatives.

* var                          (use val or lazy val instead)
* for without a yield          (this means there is a side effect in the for, return a new list instead)
* while loop                   (the loop control variable will be mutable, and the loop will be side effecting, use a fold or tail recursion instead)
* foreach                      (will be side effecting, use map() to return a new list instead)
* buffer                       (use a list comprehension such as map() or flatMap() instead)
* mutable collection           (combine immutable collections with list comprehensions, fold, and/or tail recursion)
* mutable object               (create an immutable object and methods to transform into the same type of immutable object)
* defensive copy of collection (don't need this if you are using a immutable collections)

It is worth noting that it is possible to implement a function that is referentially transparent from the outside, that uses mutable and imperative style on the inside.
This can be done by being careful not to expose any mutability from the parameter list or return value.

Design for testability
===
Try to separate your logic from your interactions with the environment, by hiding these types of things behind a contract (in Scala, a trait with no implementations)
For your classes with logic, this makes it easy to unit test, while limiting the scope of fakes you create to simulate the environment.
For your classes that integrate with the environment, since there is no logic, it limits the number of integration tests you need.
Here are some examples of things you might want to hide behind a contract:

* random
* system clock
* local time zone
* environment variable
* network
* filesystem
* database
* default character encoding
* default newline separator
* default file separator

Don't use misleading styles
===
Don't try to make side-effecting and imperative styles 'look' functional or declarative.
For example, don't use a .map() if you are side effecting, use a .foreach() to make what you are actually doing more obvious.
Using a .map() when you should use .foreach() can cause your code to not work since .map() is sometimes evaluated lazy, where .foreach() is never lazy.

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

JavaConversions
===
When interacting with Java code, understand how to switch between Scala and Java collections

* The scala.collection.JavaConversions class provides both two-way and one-way conversions, make sure you understand them
* Remember that Scala obviates the need for boxing because it treats primitives the same way as any other object.  A consequence of this is that when you are converting to/from Java collections, you will have to code any necessary boxing/unboxing yourself.  You may find import aliasing useful when doing this.

        import java.lang.{Long=>JavaLong}.

Cake Pattern
===
Do not introduce the Scala "cake" pattern.
It has been abandoned here at CJ in favor of constructor injection (and setter injection when constructor injection is not an option).
While the Scala "cake" pattern has some very neat features, it has been found to cause difficulty with testing.
One problem is that to use something inside the cake pattern, you have to pull in every layer of the cake, even if those layers are not in scope of your test.
This leads to creating more complicated test objects, and finally another problem when you try to use one of these objects from a different module, as maven does not allow dependencies between modules within the context of the "test" scope.
Also, the cake pattern difficult to combine with plain code, as it tends to force you to place all dependencies within the cake pattern once you start using it.
This forces you to make even the simplest components aware of the cake pattern, which is complexity you would not have to pay for if you were using constructor or setter injection.

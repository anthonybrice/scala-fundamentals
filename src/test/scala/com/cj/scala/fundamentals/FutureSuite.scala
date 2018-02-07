package com.cj.scala.fundamentals

import java.util.concurrent.TimeUnit

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

// Don't use Await except for samples or tests (this is a sample)
// Don't use an infinite duration except in samples
// The only reason I can think of to use await in production code is to cleanly shut down (don't use infinite duration)
class FutureSuite extends FunSuite {
  val maxTestTime = FiniteDuration(100, TimeUnit.MILLISECONDS)

  test("join two concurrent processes") {
    // start computing right away
    val futureTwo = Future[Int] {
      2
    }

    // start computing right away
    val futureThree = Future[Int] {
      3
    }

    // compose a new future from the others without waiting for any to be complete
    val futureResult = for {
      two <- futureTwo
      three <- futureThree
    } yield two * three

    // if you are testing, it is ok to wait until we are caught up
    // just make sure the test does not use the max time up unless it actually needs it
    // for example, don't Thread.sleep(maxTestTime) to give the test a chance to catch up
    // you would be making the test take longer than necessary
    Await.ready(futureResult, maxTestTime)

    // the future can be in three states: successful completion, failure, still running
    val resultDescription = futureResult.value match {
      case Some(Success(value)) => "success: " + value
      case Some(Failure(exception)) => "failure: " + exception.getMessage
      case None => "don't have an answer yet"
    }

    assert(resultDescription === "success: 6")
  }

  test("ILLUSTRATION ONLY, DON'T DO THIS - join to concurrent processes in serial") {
    // start computing right away
    val futureTwo = Future[Int] {
      2
    }

    // start computing right away
    val futureThree = Future[Int] {
      3
    }

    val two = Await.result(futureTwo, maxTestTime)
    val three = Await.result(futureThree, maxTestTime)

    val futureResult = Future[Int] {
      two * three
    }

    Await.ready(futureResult, maxTestTime)

    // the future can be in three states: successful completion, failure, still running
    val resultDescription = futureResult.value match {
      case Some(Success(value)) => "success: " + value
      case Some(Failure(exception)) => "failure: " + exception.getMessage
      case None => "don't have an answer yet"
    }

    assert(resultDescription === "success: 6")
  }

  test("future stubbing ab") {
    // given
    val futureRunner = new FutureRunnerStub
    val events = new ArrayBuffer[String]()
    val futureA = futureRunner.runInFuture {
      events.append("a event")
      "a result"
    }
    val futureB = futureRunner.runInFuture {
      events.append("b event")
      "b result"
    }

    // when
    // resolve a first, then b
    futureRunner.promiseResolvers(0)()
    futureRunner.promiseResolvers(1)()

    // then
    assert(events === Seq("a event", "b event"))
    assert(futureA.value === Some(Success("a result")))
    assert(futureB.value === Some(Success("b result")))
  }

  test("future stubbing ba") {
    // given
    val futureRunner = new FutureRunnerStub
    val events = new ArrayBuffer[String]()
    val futureA = futureRunner.runInFuture {
      events.append("a event")
      "a result"
    }
    val futureB = futureRunner.runInFuture {
      events.append("b event")
      "b result"
    }

    // when
    // resolve b first, then a
    futureRunner.promiseResolvers(1)()
    futureRunner.promiseResolvers(0)()

    // then
    assert(events === Seq("b event", "a event"))
    assert(futureA.value === Some(Success("a result")))
    assert(futureB.value === Some(Success("b result")))
  }

  // how do I test non-determinism?
  // the answer is always the same
  // hide the non determinism behind a contract
  trait FutureRunner {
    def runInFuture[T](block: => T): Future[T]
  }

  // not used in the test
  // included to let you know what the production version would look like
  class FutureRunnerWithExecutionContext(notifyRegardingException: Throwable => Unit)
                                        (implicit executionContext: ExecutionContext) extends FutureRunner {
    override def runInFuture[T](block: => T): Future[T] = {
      Future {
        try {
          block
        } catch {
          case ex: Throwable =>
            notifyRegardingException(ex)
            throw ex
        }
      }(executionContext)
    }
  }

  class FutureRunnerStub extends FutureRunner {
    val promiseResolvers: ArrayBuffer[() => Unit] = ArrayBuffer()

    override def runInFuture[T](block: => T): Future[T] = {
      val promise = Promise[T]
      val resolvePromise: () => Unit = () => {
        promise.success(block)
      }
      promiseResolvers.append(resolvePromise)
      promise.future
    }
  }

}

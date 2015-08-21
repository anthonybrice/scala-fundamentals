package com.cj.scala.fundamentals

import org.scalatest.FunSuite

import scala.util.Try

class OptionSuite extends FunSuite {

  case class User(name: String, password: String)

  //an option gives you a means to communicate that something could be null
  def searchForUserNamed(userName: String): Option[User] = {
    if (userName == "Alice")
      Some(User("Alice", "alicePassword"))
    else
      None
  }

  test("find user by name") {
    val actual = searchForUserNamed("Alice")
    assert(actual === Some(User("Alice", "alicePassword")))
  }

  test("search for user by name but don't find") {
    val actual = searchForUserNamed("Bob")
    assert(actual === None)
  }

  test("display search result") {
    def searchResult(userName: String) = searchForUserNamed(userName) match {
      case Some(user) => s"found user ${user.name} with password ${user.password}"
      case None => s"didn't find user $userName"
    }
    assert(searchResult("Alice") === "found user Alice with password alicePassword")
    assert(searchResult("Bob") === "didn't find user Bob")
  }

  test("we can `lift` ordinary functions to process Option!"){

    def legacyJavaFunction(id : String, age : Int, weight : Double ) : String = {
      s"Java Call Succeeded! id $id"
    }

    val maybeAge  = Try("1".toInt).toOption
    val maybeWeight  = Try("157.4".toDouble).toOption

    val optionsHelpExpressPipelines  : (String) => Option[String] =
        n =>  maybeAge.flatMap(
          a => maybeWeight.flatMap(
            w => Option(legacyJavaFunction(n,a,w))))

    val optionsCanParticipateInForExpressionsToo =
      for { name <- Option("Fred")
            age <- maybeAge
            weight <- maybeWeight}
        yield {legacyJavaFunction(name, age, weight)}

    assert(optionsHelpExpressPipelines("Fred") ==
              optionsCanParticipateInForExpressionsToo)

    val optionsCarryValuesThroughPipelines =
        Try("123".toInt).toOption.flatMap( (i: Int) =>  optionsHelpExpressPipelines(i.toString))

    assert( optionsCarryValuesThroughPipelines ===
      Some("Java Call Succeeded! id 123")) // yay!


    val optionsCanIndicateFailureAndShortCircuitPipelinesToo =
      Try("Oh Noez!".toInt).toOption.flatMap( (i: Int) =>  optionsHelpExpressPipelines(i.toString))

    assert( optionsCanIndicateFailureAndShortCircuitPipelinesToo ===
          None )// boo!

  }

}

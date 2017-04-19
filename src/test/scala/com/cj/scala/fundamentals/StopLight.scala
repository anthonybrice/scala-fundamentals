package com.cj.scala.fundamentals

sealed trait StopLight

object StopLight {

  case object Red extends StopLight

  case object Yellow extends StopLight

  case object Green extends StopLight

}
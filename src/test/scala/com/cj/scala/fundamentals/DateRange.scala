package com.cj.scala.fundamentals

import java.time.temporal.ChronoUnit
import java.time.{Instant, ZoneId, ZonedDateTime}

import scala.collection.mutable.ArrayBuffer

sealed abstract case class DateRange(name: String) {
  DateRange.valuesBuffer += this

  def rangeBeginInclusiveToEndExclusive(now: Instant, zoneId: ZoneId): (ZonedDateTime, ZonedDateTime)
}

object DateRange {
  private val valuesBuffer = new ArrayBuffer[DateRange]
  lazy val values: Seq[DateRange] = valuesBuffer
  val LastSevenDays = new DateRange("Last 7 Days") {
    def rangeBeginInclusiveToEndExclusive(now: Instant, zoneId: ZoneId): (ZonedDateTime, ZonedDateTime) =
      (ZonedDateTime.ofInstant(now, zoneId).truncatedTo(ChronoUnit.DAYS).minusDays(7),
        ZonedDateTime.ofInstant(now, zoneId).truncatedTo(ChronoUnit.DAYS).minusDays(0))
  }
  val LastThirtyDays = new DateRange("Last 30 Days") {
    def rangeBeginInclusiveToEndExclusive(now: Instant, zoneId: ZoneId): (ZonedDateTime, ZonedDateTime) =
      (ZonedDateTime.ofInstant(now, zoneId).truncatedTo(ChronoUnit.DAYS).minusDays(30),
        ZonedDateTime.ofInstant(now, zoneId).truncatedTo(ChronoUnit.DAYS).minusDays(0))
  }
  val LastQuarter = new DateRange("Last Quarter") {
    def rangeBeginInclusiveToEndExclusive(now: Instant, zoneId: ZoneId): (ZonedDateTime, ZonedDateTime) = {
      val today = ZonedDateTime.ofInstant(now, zoneId).truncatedTo(ChronoUnit.DAYS)
      val month = today.getMonthValue
      val quarter = (month + 2) / 3
      val monthAtBeginningOfQuarter = quarter * 3 - 2
      val endQuarter = today.withDayOfMonth(1).withMonth(monthAtBeginningOfQuarter)
      val beginQuarter = endQuarter.minusMonths(3)
      (beginQuarter, endQuarter)
    }
  }
  val LastYear = new DateRange("Last Year") {
    def rangeBeginInclusiveToEndExclusive(now: Instant, zoneId: ZoneId): (ZonedDateTime, ZonedDateTime) = {
      val today = ZonedDateTime.ofInstant(now, zoneId).truncatedTo(ChronoUnit.DAYS)
      val year = today.getYear
      val lastYear = today.withYear(year - 1).withDayOfYear(1)
      val thisYear = today.withYear(year).withDayOfYear(1)
      (lastYear, thisYear)
    }
  }
  val YearToDate = new DateRange("Year to Date") {
    def rangeBeginInclusiveToEndExclusive(now: Instant, zoneId: ZoneId): (ZonedDateTime, ZonedDateTime) = {
      val today = ZonedDateTime.ofInstant(now, zoneId).truncatedTo(ChronoUnit.DAYS)
      val beginningOfYear = today.withDayOfYear(1)
      (beginningOfYear, today)
    }
  }
  val QuarterToDate = new DateRange("Quarter to Date") {
    def rangeBeginInclusiveToEndExclusive(now: Instant, zoneId: ZoneId): (ZonedDateTime, ZonedDateTime) = {
      val today = ZonedDateTime.ofInstant(now, zoneId).truncatedTo(ChronoUnit.DAYS)
      val month = today.getMonthValue
      val quarter = (month + 2) / 3
      val monthAtBeginningOfQuarter = quarter * 3 - 2
      val beginQuarter = today.withDayOfMonth(1).withMonth(monthAtBeginningOfQuarter)
      (beginQuarter, today)
    }
  }
}
package com.cj.scala.fundamentals

import java.time._

import org.scalatest.FunSuite

class EnumStyleSuite extends FunSuite {
  test("iterate over enumeration") {
    val actual = DateRange.values.map(_.name)
    val expected = Seq("Last 7 Days", "Last 30 Days", "Last Quarter", "Last Year", "Year to Date", "Quarter to Date")
    assert(actual === expected)
  }

  test("polymorphism on enumeration") {
    val date = LocalDate.of(2014, 8, 23)
    val time = LocalTime.of(3, 15, 20)
    val zone = ZoneId.of("UTC")
    val now = ZonedDateTime.of(date, time, zone)
    testPolymorphism(DateRange.LastSevenDays, now, "2014-08-16T00:00Z[UTC]", "2014-08-23T00:00Z[UTC]")
    testPolymorphism(DateRange.LastThirtyDays, now, "2014-07-24T00:00Z[UTC]", "2014-08-23T00:00Z[UTC]")
    testPolymorphism(DateRange.LastQuarter, now, "2014-04-01T00:00Z[UTC]", "2014-07-01T00:00Z[UTC]")
    testPolymorphism(DateRange.LastYear, now, "2013-01-01T00:00Z[UTC]", "2014-01-01T00:00Z[UTC]")
    testPolymorphism(DateRange.YearToDate, now, "2014-01-01T00:00Z[UTC]", "2014-08-23T00:00Z[UTC]")
    testPolymorphism(DateRange.QuarterToDate, now, "2014-07-01T00:00Z[UTC]", "2014-08-23T00:00Z[UTC]")
  }

  def testPolymorphism(dateRange: DateRange, when: ZonedDateTime, expectedFromString: String, expectedToString: String): Unit = {
    val (from, to) = dateRange.rangeBeginInclusiveToEndExclusive(when.toInstant, when.getZone)
    assert(from.toString === expectedFromString)
    assert(to.toString === expectedToString)
  }
}

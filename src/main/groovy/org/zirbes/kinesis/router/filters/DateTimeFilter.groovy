package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic

import org.joda.time.Days
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.ReadablePeriod

/**
 * Filter on weekdays or time of day
 */
@CompileStatic
class DateTimeFilter implements DataFilter {

    final List<Integer> daysOfWeek = (1..7) as List<Integer>
    LocalTime startTime = LocalTime.MIDNIGHT
    ReadablePeriod period = Days.ONE

    DateTimeFilter() { }

    DateTimeFilter(LocalTime startTime, ReadablePeriod period) {
        this.startTime = startTime
        this.period = period
    }

    DateTimeFilter(LocalTime startTime, ReadablePeriod period, List<Integer> daysOfWeek) {
        this.startTime = startTime
        this.period = period
        this.daysOfWeek = daysOfWeek
    }

    @Override
    byte[] filter(byte[] data) {
        LocalDateTime now = LocalDateTime.now()
        if (
            now.dayOfWeek in daysOfWeek &&
            now.toLocalTime() >= startTime &&
            startTime.plus(period) >= now.toLocalTime()
        ) { return data }
    }

}

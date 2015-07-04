package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.util.Locale

import org.joda.time.Days
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.MutablePeriod
import org.joda.time.ReadWritablePeriod
import org.joda.time.ReadablePeriod
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder
import org.joda.time.format.PeriodParser

/**
 * Filter on weekdays or time of day
 */
@CompileStatic
@Slf4j
class DateTimeFilter implements DataFilter {

    @Override FilterType getType() { FilterType.DATETIME }

    Set<Integer> daysOfWeek = [1,2,3,4,5,6,7] as Set
    LocalTime startTime = LocalTime.MIDNIGHT
    ReadablePeriod period = Days.ONE

    static final Map<String, Integer> WEEKDAYS = [
        'M':  1,
        'Tu': 2,
        'W':  3,
        'Th': 4,
        'F':  5,
        'Sa': 6,
        'Su': 7
    ]

    DateTimeFilter() { }

    /**
     * Parse a config param into a date filter
     * 13:45:00,3h 30m 15s,MTuWThFSaSu
     */
    DateTimeFilter(String config) {
        String[] parts = config.split(',')
        startTime = new LocalTime(parts[0])
        if (parts.size() > 1) { setPeriodFromString(parts[1]) }
        if (parts.size() > 2) { setWeekDaysFromString(parts[2]) }
    }

    void setPeriodFromString(String periodText) {

        PeriodParser periodParser = new PeriodFormatterBuilder().printZeroNever()
                .appendDays().appendSuffix("d").appendSeparator(" ")
                .appendHours().appendSuffix("h").appendSeparator(" ")
                .appendMinutes().appendSuffix("m").appendSeparator(" ")
                .appendSeconds().appendSuffix("s")
                .toParser()

        ReadWritablePeriod newPeriod = new MutablePeriod()
        periodParser.parseInto(newPeriod, periodText, 0, Locale.US)
        this.period = newPeriod

    }

    void setWeekDaysFromString(String weekdays) {

        WEEKDAYS.each{ String wd, Integer dow ->
            if (weekdays.contains(wd)) {
                this.daysOfWeek << dow
            } else {
                this.daysOfWeek.remove(dow)
            }
        }
    }

    DateTimeFilter(LocalTime startTime, ReadablePeriod period) {
        this.startTime = startTime
        this.period = period
    }

    DateTimeFilter(LocalTime startTime, ReadablePeriod period, List<Integer> daysOfWeek) {
        this.startTime = startTime
        this.period = period
        this.daysOfWeek = daysOfWeek as Set
    }

    @Override
    String toString() {
        PeriodFormatter periodPrinter = new PeriodFormatterBuilder().printZeroNever()
                .appendDays().appendSuffix("d").appendSeparator(" ")
                .appendHours().appendSuffix("h").appendSeparator(" ")
                .appendMinutes().appendSuffix("m").appendSeparator(" ")
                .appendSeconds().appendSuffix("s")
                .toFormatter()
        String periodText = periodPrinter.print(period)
        String dowText = ''
        WEEKDAYS.each{ String k, Integer v ->
            if (v in daysOfWeek) { dowText += k }
        }

        return "${type}: ${startTime} + ${periodText} (${dowText})"
    }


    @Override
    byte[] filter(byte[] data) {
        LocalDateTime now = LocalDateTime.now()
        if (
            now.dayOfWeek in daysOfWeek &&
            now.toLocalTime() >= startTime &&
            (
                period == Days.ONE ||
                startTime.plus(period) >= now.toLocalTime()
            )
        ) {
            return data
        } else {
            log.warn "filtering data due to datetime."

            if (!(now.dayOfWeek in daysOfWeek)) {
                log.warn 'day of week not allowed'
            }
            if (now.toLocalTime() < startTime) {
                log.warn 'time too early'
            }
            if (startTime.plus(period) < now.toLocalTime()) {
                log.warn 'time too late'
            }
            return [] as byte[]
        }
    }

}

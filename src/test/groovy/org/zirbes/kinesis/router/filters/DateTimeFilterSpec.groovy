package org.zirbes.kinesis.router.filters

import org.joda.time.LocalDate
import org.joda.time.LocalTime

import spock.lang.Specification

class DateTimeFilterSpec extends Specification {


    void 'date time filter allows data by default'() {
        given:
        byte[] data = 'Sample Data'.bytes
        DataFilter filter = new DateTimeFilter()

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered == data
    }

    void 'date time filter allows data if within time window'() {
        given:
        byte[] data = 'Sample Data'.bytes
        LocalTime start = LocalTime.now().minusHours(1)
        String duration = '2h'
        DataFilter filter = new DateTimeFilter("${start},${duration}")

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered == data
    }

    void 'date time filter skips data after window'() {
        given:
        byte[] data = 'Sample Data'.bytes
        LocalTime start = LocalTime.now().minusHours(2)
        String duration = '1h'
        DataFilter filter = new DateTimeFilter("${start},${duration}")

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered.size() == 0
    }

    void 'date time filter skips data before window'() {
        given:
        byte[] data = 'Sample Data'.bytes
        LocalTime start = LocalTime.now().plusHours(1)
        String duration = '2h'
        DataFilter filter = new DateTimeFilter("${start},${duration}")

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered.size() == 0
    }

    void 'date time filter skips data if not weekday'() {
        given:
        byte[] data = 'Sample Data'.bytes
        LocalTime start = LocalTime.now().minusHours(2)
        String duration = '5h'
        Integer dow = LocalDate.now().plusDays(1).dayOfWeek
        String weekday = DateTimeFilter.WEEKDAYS.find{ k, v -> v == dow }.key
        DataFilter filter = new DateTimeFilter("${start},${duration},${weekday}")

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered.size() == 0
    }

}

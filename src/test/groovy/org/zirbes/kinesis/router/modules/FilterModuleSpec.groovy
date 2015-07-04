package org.zirbes.kinesis.router.modules

import org.zirbes.kinesis.router.filters.FilterChain
import org.zirbes.kinesis.router.filters.FilterPrinter
import org.zirbes.kinesis.router.filters.FilterType
import org.zirbes.kinesis.router.filters.OrFilterChain

import spock.lang.Specification

class FilterModuleSpec extends Specification {

    void 'can build filter chain from config'() {
        given:
        Map config = [
            and: [
                or: [
                    datetime: '12:34:56,6h,MTuWThF',
                    open: true
                ],
                rate: '0.5'
            ],
            or: [
                datetime: '9:15:45,12h',
                and: [
                    rate: '0.9',
                    open: true
                ]
            ]
        ]

        when:
        FilterModule module = new FilterModule(config)
        module.configure()

        and:
        println new FilterPrinter().print(module.filter)

        then:
        module.filter.type == FilterType.OR

        when:
        OrFilterChain baseFilter = module.filter

        then:
        baseFilter.filters.size() == 2
        baseFilter.filters[0].type == FilterType.AND
        baseFilter.filters[1].type == FilterType.OR

        when:
        FilterChain andFilter = baseFilter.filters[0]
        FilterChain orFilter = baseFilter.filters[1]

        then:
        orFilter.filters.size() == 2
        orFilter.filters[0].type == FilterType.DATETIME
        orFilter.filters[1].type == FilterType.AND

        andFilter.filters.size() == 2
        andFilter.filters[0].type == FilterType.OR
        andFilter.filters[1].type == FilterType.RATE

    }

}

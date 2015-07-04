package org.zirbes.kinesis.router.filters

import spock.lang.Specification
import spock.lang.Unroll

class FilterTypeSpec extends Specification {

    @Unroll
    void 'can build #config filter from strings'() {
        given:
        FilterType type = FilterType.fromString(config)

        when:
        DataFilter filter = type.filter(setting)

        then:
        filter.class == clazz

        where:
        config     | setting               | clazz
        'and'      | null                  | AndFilterChain
        'datetime' | '12:34:56,6h,MTuWThF' | DateTimeFilter
        'open'     | null                  | OpenFilter
        'or'       | null                  | OrFilterChain
        'rate'     | '0.25'                | RateFilter
    }

}

package org.zirbes.kinesis.router.filters

import spock.lang.Specification

class AndFilterChainSpec extends Specification {

    void 'and filter passes data through if all matches'() {
        given:
        byte[] data = 'Sample Data'.bytes
        DataFilter filter = new AndFilterChain()
        filter.with(new OpenFilter())
        filter.with(new RateFilter(1.0d))

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered == data
    }

    void 'and filter skips data if any do not match'() {
        given:
        byte[] data = 'Sample Data'.bytes
        DataFilter filter = new AndFilterChain()
        filter.with(new OpenFilter())
        filter.with(new RateFilter(0.0d))

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered.size() == 0
    }


}

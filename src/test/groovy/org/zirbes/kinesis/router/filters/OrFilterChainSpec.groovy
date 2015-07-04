package org.zirbes.kinesis.router.filters

import spock.lang.Specification

class OrFilterChainSpec extends Specification {

    void 'or filter passes data through if one matches'() {
        given:
        byte[] data = 'Sample Data'.bytes
        DataFilter filter = new OrFilterChain()
        filter.with(new OpenFilter())
        filter.with(new RateFilter(0.0d))

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered == data
    }

    void 'or filter skips data if none matches'() {
        given:
        byte[] data = 'Sample Data'.bytes
        DataFilter filter = new OrFilterChain()
        filter.with(new RateFilter(0.0d))
        filter.with(new RateFilter(0.0d))

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered.size() == 0
    }


}

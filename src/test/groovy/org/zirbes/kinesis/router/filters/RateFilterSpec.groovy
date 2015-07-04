package org.zirbes.kinesis.router.filters

import spock.lang.Specification
import spock.lang.Unroll

class RateFilterSpec extends Specification {

    @Unroll
    void 'rate filter filters at rate of #rate data through'() {
        given:
        byte[] data = 'Sample Data'.bytes
        DataFilter filter = new RateFilter(rate)
        Integer hits = 0

        when:
        1000.times {
            byte[] filtered = filter.filter(data)
            if (filtered.size() > 0) {
                hits++
            }
        }
        println "${hits} for rate ${rate}"

        then:
        hits in range

        where:
        rate | range
        1.0d | 1000..1000
        0.8d | 750..850
        0.1d | 50..150
    }

}

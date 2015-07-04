package org.zirbes.kinesis.router.filters

import spock.lang.Specification

class OpenFilterSpec extends Specification {

    DataFilter filter = new OpenFilter()

    void 'open filter passes data through'() {
        given:
        byte[] data = 'Sample Data'.bytes

        when:
        byte[] filtered = filter.filter(data)

        then:
        filtered == data
    }

}

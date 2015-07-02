package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic

import java.security.SecureRandom
import java.util.Random

/**
 * Percentage filter.
 */
@CompileStatic
class RateFilter implements DataFilter {

    final Double rate
    final Random random = new SecureRandom()

    RateFilter(Double rate) {
        this.rate = rate
    }

    @Override
    byte[] filter(byte[] data) {
        double dice = random.nextDouble()
        if (dice <= rate) { return data }
        return [] as byte[]
    }

}

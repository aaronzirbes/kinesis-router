package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic

/**
 * Interface for filtering data before forwarding to kinesis
 */
@CompileStatic
class AndFilterChain extends FilterChain {

    @Override FilterType getType() { FilterType.AND }

    AndFilterChain() {
        this.filters = []
    }

    /**
     * Inspect data, return an empty byte array if it shouldn't go through.
     */
    @Override
    byte[] filter(byte[] data) {
        byte[] output = data
        filters.each{ output = it.filter(data) }
        return output
    }

}

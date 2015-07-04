package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic

/**
 * Interface for filtering data before forwarding to kinesis
 */
@CompileStatic
class OrFilterChain extends FilterChain {

    @Override FilterType getType() { FilterType.OR }

    OrFilterChain() {
        this.filters = []
    }

    OrFilterChain(List<DataFilter> filters) {
        this.filters = filters
    }

    /**
     * Inspect data, return an empty byte array if it shouldn't go through.
     */
    @Override
    byte[] filter(byte[] data) {
        if (filters.any{ it.filter(data) } ) {
            return data
        } else {
            return [] as byte[]
        }
    }

}

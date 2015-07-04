package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic

/**
 * Interface for filtering data before forwarding to kinesis
 */
@CompileStatic
abstract class FilterChain implements DataFilter {

    List<DataFilter> filters = []

    /**
     * Add filter to the chain.
     */
    FilterChain with(DataFilter filter) {
        this.filters << filter
        return this
    }

    @Override
    String toString() { type }

    /**
     * Inspect data, return an empty byte array if it shouldn't go through.
     */
    abstract byte[] filter(byte[] data)

}

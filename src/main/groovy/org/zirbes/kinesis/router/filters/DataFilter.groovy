package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic

/**
 * Interface for filtering data before forwarding to kinesis
 */
@CompileStatic
interface DataFilter {

    FilterType getType()

    /**
     * Inspect data, return an empty byte array if it shouldn't go through.
     */
    byte[] filter(byte[] data)

}

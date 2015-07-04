package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic

/**
 * Passthrough no-op filter.
 */
@CompileStatic
class OpenFilter implements DataFilter {

    @Override FilterType getType() { FilterType.OPEN }

    @Override
    byte[] filter(byte[] data) {
        return data
    }

    @Override
    String toString() { type }

}

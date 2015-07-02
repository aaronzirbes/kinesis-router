package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic

/**
 * Passthrough no-op filter.
 */
@CompileStatic
class OpenFilter implements DataFilter {

    @Override
    byte[] filter(byte[] data) {
        return data
    }

}

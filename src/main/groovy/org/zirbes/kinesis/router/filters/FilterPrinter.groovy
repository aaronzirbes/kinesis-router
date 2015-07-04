package org.zirbes.kinesis.router.filters

import groovy.transform.CompileStatic

@CompileStatic
class FilterPrinter {

    StringWriter sw = new StringWriter()

    String print(DataFilter filter) {
        ammend filter, 0
        return sw.toString()
    }

    protected void ammend(DataFilter filter, Integer depth) {
        sw << ('  ' * depth) + " * ${filter}"
        if (filter.type in FilterType.CHAINS) {
            FilterChain chain = (FilterChain) filter
            chain.filters.each{ DataFilter subFilter ->
                sw << '\n'
                ammend subFilter, depth + 1
            }
        }
    }

}

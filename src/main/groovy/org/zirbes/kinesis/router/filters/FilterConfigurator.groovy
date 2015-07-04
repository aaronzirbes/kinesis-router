package org.zirbes.kinesis.router.filters

class FilterConfigurator {

    DataFilter fromConfig(Map<String, Object> filterConfig) {
        DataFilter rootFilter
        List<DataFilter> filters = []
        filterConfig.each{ String filterName, options ->
            filters << buildFilter(filterName, options)
        }
        if (filters.size() == 0) {
            rootFilter = new OpenFilter()
        } else if (filters.size() == 1) {
            rootFilter = filters[0]
        } else if (filters.size() > 1) {
            rootFilter = new OrFilterChain(filters)
        }
        return rootFilter
    }

    protected DataFilter buildFilter(String type, Object options) {
        DataFilter filter = FilterType.fromString(type).filter(options.toString())
        if (filter.type in FilterType.CHAINS && options instanceof Map) {
            FilterChain chain = (FilterChain) filter
            ((Map<String, Object>) options).each{ String filterType, opts ->
                DataFilter subFilter = buildFilter(filterType, opts)
                chain.with(subFilter)
            }
            filter = chain
        }
        return filter
    }

}

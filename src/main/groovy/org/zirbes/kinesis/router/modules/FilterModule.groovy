package org.zirbes.kinesis.router.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.zirbes.kinesis.router.filters.DataFilter
import org.zirbes.kinesis.router.filters.FilterConfigurator
import org.zirbes.kinesis.router.filters.FilterPrinter
import org.zirbes.kinesis.router.filters.OpenFilter

@CompileStatic
@Slf4j
class FilterModule extends AbstractModule {

    DataFilter filter = new OpenFilter()

    Map<String, Object> filterConfig

    FilterModule(Map<String, Object> filterConfig) {
        this.filterConfig = filterConfig
    }

    @Override
    protected void configure() {
        FilterConfigurator configurator = new FilterConfigurator()
        this.filter = configurator.fromConfig(filterConfig)

        String filterTree = new FilterPrinter().print(this.filter)
        log.info "Filter Configuration:\n${filterTree}"
    }


    @Provides
    @Singleton
    DataFilter providesDataFilter() {
        return filter
    }

}

package org.zirbes.kinesis.router.modules

import com.amazonaws.kinesis.producer.Configuration
import com.amazonaws.kinesis.producer.KinesisProducer
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Scopes
import com.google.inject.Singleton

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.zirbes.kinesis.router.KinesisActionChain
import org.zirbes.kinesis.router.config.KinesisConfiguration
import org.zirbes.kinesis.router.handlers.HealthHandler
import org.zirbes.kinesis.router.handlers.KinesisHandler
import org.zirbes.kinesis.router.handlers.VersionHandler

@CompileStatic
@Slf4j
class KinesisModule extends AbstractModule {

    static final String DEFAULT_STREAM = 'default'
    static final String DEFAULT_REGION = 'us-east-1'

    private final String kinesisStream
    private final String region

    KinesisModule(Map kinesisConfig) {
        this.kinesisStream = kinesisConfig.stream ?: DEFAULT_STREAM
        this.region = kinesisConfig.region ?: DEFAULT_REGION
    }

    @Override
    protected void configure() {
        bind(KinesisActionChain).in(Scopes.SINGLETON)
        bind(VersionHandler).in(Scopes.SINGLETON)
        bind(HealthHandler).in(Scopes.SINGLETON)
        bind(KinesisHandler).in(Scopes.SINGLETON)
    }

    @Provides
    @Singleton
    KinesisProducer provideKinesisProducer() {
        new KinesisProducer(configureProducer())
    }

    @Provides
    @Singleton
    KinesisConfiguration providesKinesisConfiguration() {
        new KinesisConfiguration(region: region, stream: kinesisStream)
    }

    protected Configuration configureProducer() {
        Configuration config = new Configuration()
        config.region = region
        config.maxConnections = 1
        config.requestTimeout = 5000
        config.recordMaxBufferedTime = 10000
        return config
    }

}

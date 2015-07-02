package org.zirbes.kinesis.router.handlers

import com.amazonaws.kinesis.producer.KinesisProducer
import com.amazonaws.kinesis.producer.UserRecordResult
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.joda.time.LocalDateTime
import org.zirbes.kinesis.router.config.KinesisConfiguration
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import java.nio.ByteBuffer

import static ratpack.jackson.Jackson.json

@CompileStatic
@Slf4j
class KinesisHandler extends GroovyHandler {

    protected final KinesisProducer producer
    protected final KinesisConfiguration config
    protected final HealthHandler health

    @Inject
    KinesisHandler(KinesisProducer producer,
                   KinesisConfiguration config,
                   HealthHandler health) {
        this.producer = producer
        this.config = config
        this.health = health
    }

    @Override
    protected void handle(GroovyContext context) {

        byte[] bytes = context.request.body.bytes
        ByteBuffer data = ByteBuffer.wrap(bytes)
        String key = "${UUID.randomUUID()}:${LocalDateTime.now()}"
        Futures.addCallback(producer.addUserRecord(config.stream, key, data), new KinesisWriteCallback(key, health))

        context.response.status(200)
        context.render(json([record: key]))
    }

    @Slf4j
    protected static class KinesisWriteCallback implements FutureCallback<UserRecordResult> {

        protected final String key
        protected final HealthHandler health

        KinesisWriteCallback(String key, HealthHandler health) {
            this.key = key
            this.health = health
        }

        @Override
        public void onFailure(Throwable t) {
            log.error "failed to write record key=${key} to kinesis", t
            health.status.compareAndSet(200, 400)
        }

        @Override
        public void onSuccess(UserRecordResult result) {
            log.debug "added record key=${key} with sequence #${result.sequenceNumber}"
            health.status.compareAndSet(400, 200)
        }
    }

}

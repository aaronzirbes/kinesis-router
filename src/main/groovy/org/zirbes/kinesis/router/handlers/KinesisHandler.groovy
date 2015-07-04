package org.zirbes.kinesis.router.handlers

import com.amazonaws.kinesis.producer.KinesisProducer
import com.amazonaws.kinesis.producer.UserRecordResult
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.inject.Inject

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.nio.ByteBuffer

import org.joda.time.LocalDateTime
import org.zirbes.kinesis.router.config.KinesisConfiguration
import org.zirbes.kinesis.router.filters.DataFilter

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static ratpack.jackson.Jackson.json

@CompileStatic
@Slf4j
class KinesisHandler extends GroovyHandler {

    protected final KinesisProducer producer
    protected final KinesisConfiguration config
    protected final HealthHandler health
    protected final DataFilter dataFilter

    @Inject
    KinesisHandler(KinesisConfiguration config,
                   DataFilter dataFilter,
                   KinesisProducer producer,
                   HealthHandler health) {
        this.config = config
        this.dataFilter = dataFilter
        this.health = health
        this.producer = producer
    }

    @Override
    protected void handle(GroovyContext context) {
        String key = "${UUID.randomUUID()}:${LocalDateTime.now()}"
        String message = 'queued'
        int status = 204

        byte[] bytes = context.request.body.bytes
        if (bytes.size() == 0) {
            message = 'empty request'
        } else {
            byte[] filtered = dataFilter.filter(bytes)
            if (filtered.size() == 0) {
                status = 202
                message = 'filtered request'
            } else {
                status = 201
                String byteText = new String(bytes, 'UTF-8')
                log.trace "posting data: ${byteText}"

                ByteBuffer data = ByteBuffer.wrap(bytes)
                Futures.addCallback(producer.addUserRecord(config.stream, key, data), new KinesisWriteCallback(key, health))
            }
        }

        context.response.status(status)
        context.render(json([record: key, message: message]))
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
            log.info "added record key=${key} with sequenceNumber=${result.sequenceNumber} to shard=${result.shardId}"
            health.status.compareAndSet(400, 200)
        }
    }

}

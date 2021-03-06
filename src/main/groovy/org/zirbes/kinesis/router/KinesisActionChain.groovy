package org.zirbes.kinesis.router

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.zirbes.kinesis.router.handlers.HealthHandler
import org.zirbes.kinesis.router.handlers.KinesisHandler
import org.zirbes.kinesis.router.handlers.VersionHandler

import ratpack.func.Action
import ratpack.groovy.Groovy
import ratpack.handling.Chain

@CompileStatic
@Slf4j
class KinesisActionChain implements Action<Chain> {

    @Override
    void execute(Chain chain) throws Exception {
        Groovy.chain(chain) {
            get('health') { HealthHandler healthHandler ->
                context.insert(healthHandler)
            }
            get('version') { VersionHandler versionHandler ->
                context.insert(versionHandler)
            }
            post('data') { KinesisHandler kinesisHandler ->
                context.insert(kinesisHandler)
            }
        }
    }
}

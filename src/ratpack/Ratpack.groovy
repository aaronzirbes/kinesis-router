import org.zirbes.kinesis.router.handlers.KinesisErrorHandler
import org.zirbes.kinesis.router.modules.FilterModule
import org.zirbes.kinesis.router.modules.KinesisModule
import org.zirbes.kinesis.router.KinesisActionChain

import ratpack.config.ConfigData
import ratpack.error.ServerErrorHandler
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.ratpack

ratpack {
    serverConfig {
        port 8080
    }
    bindings {
        ConfigData configData = ConfigData.of()
                .yaml(ClassLoader.getSystemResource('application.yml'))
                .env()
                .sysProps()
                .build()
        bindInstance(ConfigData, configData)
        bindInstance(ServerErrorHandler, new KinesisErrorHandler())
        add new FilterModule(configData.get('/filter', Object))
        add new KinesisModule(configData.get('/kinesis', Object))
        add JacksonModule
    }
    handlers {
        handler chain(registry.get(KinesisActionChain))
    }
}

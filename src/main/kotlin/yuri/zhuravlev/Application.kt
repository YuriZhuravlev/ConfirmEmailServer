package yuri.zhuravlev

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import yuri.zhuravlev.plugins.configureMonitoring
import yuri.zhuravlev.plugins.configureSockets

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSockets()
        configureMonitoring()
    }.start(wait = true)
}

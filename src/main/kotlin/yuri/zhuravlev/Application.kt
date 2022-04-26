package yuri.zhuravlev

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import yuri.zhuravlev.plugins.configureMonitoring
import yuri.zhuravlev.plugins.configureSockets

fun main() {
    embeddedServer(Netty, port = (System.getenv("PORT") ?: "8080").toInt()) {
        configureSockets()
        configureMonitoring()
    }.start(wait = true)
}
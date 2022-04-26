package yuri.zhuravlev

import io.ktor.server.application.*
import yuri.zhuravlev.plugins.configureMonitoring
import yuri.zhuravlev.plugins.configureSockets

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    configureSockets()
    configureMonitoring()
}
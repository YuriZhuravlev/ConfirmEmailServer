package yuri.zhuravlev.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive
import yuri.zhuravlev.data.SocketService
import java.time.Duration
import java.util.*

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val socketService = SocketService
        val connections = Collections.synchronizedMap<WebSocketServerSession, String>(HashMap())

        webSocket("/") { // websocketSession
            application.log.info("onConnect")
            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            application.log.debug("${connections[this]} -> ${frame.readText()}")
                            if (this !in connections.keys) {
                                // create connection
                                val name = frame.readText()
                                var find = false
                                connections.forEach { (key, value) ->
                                    if (value == name) {
                                        if (key.isActive)
                                            find = true
                                        else
                                            connections.remove(key)
                                        return@forEach
                                    }
                                }
                                if (find) {
                                    // имя уже занято
                                    application.log.info("Name Taken $name!")
                                    send(socketService.errorNameTaken())
                                    close()
                                } else {
                                    connections += this to name
                                    send(SocketService.SuccessName(name))
                                }
                            } else {
                                connections[this]?.let { name ->
                                    socketService.proceed(owner = name, text = frame.readText()) { to, text ->
                                        var find = false
                                        (connections.forEach { (connection, name) ->
                                            if (name == to) {
                                                find = true
                                                try {
                                                    application.log.debug("$to <- $text")
                                                    connection.send(text)
                                                } catch (e: Exception) {
                                                    application.log.debug("$to send error: ${e.message}")
                                                    connections.remove(connection)
                                                    val error = socketService.errorNotFound()
                                                    application.log.debug("$name <- $error")
                                                    send(error)
                                                }
                                                return@forEach
                                            }
                                        })
                                        if (!find) {
                                            val error = socketService.errorNotFound()
                                            application.log.debug("$name <- $error")
                                            send(error)
                                        }
                                    }
                                }
                            }
                        }
                        is Frame.Close -> {
                            application.log.info("onClose")
                            connections.remove(this)
                        }
                        else -> {
                            application.log.error("Incoming other frame")
                        }
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                application.log.info("onClose ${closeReason.await()}")
                connections.remove(this)
            } catch (e: Throwable) {
                application.log.error("onError ${closeReason.await()}")
                e.printStackTrace()
            }
        }

        get("/") {
            call.respondText("WebsocketServer")
        }
    }
}

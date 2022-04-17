package yuri.zhuravlev.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
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
        val connections = Collections.synchronizedSortedMap<WebSocketServerSession, String>(TreeMap())

        webSocket("/") { // websocketSession
            println("onConnect")
            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            if (this !in connections.keys) {
                                // create connection
                                val name = frame.readText()
                                connections += this to name
                                send("Login as [$name]")
                            } else {
                                socketService.proceed(text = frame.readText(), onSend = { text ->
                                    outgoing.send(Frame.Text("YOU SAID: $text"))
                                }, onClose = {
                                    close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                                }
                                )
                            }
                        }
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                println("onError ${closeReason.await()}")
                e.printStackTrace()
            }
        }

        get("/") {
            call.respondText("WebsocketServer")
        }
    }
}

package yuri.zhuravlev.data

import com.google.gson.Gson

object SocketService {
    val gson = Gson()

    fun proceed(text: String, onSend: suspend (String) -> Unit, onClose: suspend (String) -> Unit) {
        // TODO
    }
}
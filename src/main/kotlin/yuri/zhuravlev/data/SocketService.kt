package yuri.zhuravlev.data

import com.google.gson.Gson
import com.google.gson.JsonParseException
import yuri.zhuravlev.model.ErrorMessage
import yuri.zhuravlev.model.GetMessage
import yuri.zhuravlev.model.SendMessage

object SocketService {
    private val gson = Gson()

    suspend fun proceed(
        owner: String,
        text: String,
        onSend: suspend (to: String, text: String) -> Unit
    ) {
        try {
            val message = gson.fromJson(text, GetMessage::class.java)
            onSend(message.to, gson.toJson(SendMessage(message.message, owner)))
        } catch (e: JsonParseException) {
            e.printStackTrace()
            val result =
                gson.toJson(ErrorMessage("Ошибка сериализации, ожидается сообщение в формате {\"message\": \"text\", \"to\":\"name\"}"))
            onSend(owner, result)
        } catch (e: Exception) {
            e.printStackTrace()
            val result = gson.toJson(ErrorMessage("Неизвестная ошибка"))
            onSend(owner, result)
        }
    }

    fun errorNotFound(): String = gson.toJson(ErrorMessage("Пользователь не найден"))
}
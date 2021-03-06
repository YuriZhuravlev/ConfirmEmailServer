package yuri.zhuravlev.data

import com.google.gson.Gson
import com.google.gson.JsonParseException
import yuri.zhuravlev.model.ErrorMessage
import yuri.zhuravlev.model.GetMessage
import yuri.zhuravlev.model.SendMessage

object SocketService {
    private val gson = Gson()
    private const val SERVER = "server"

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

    fun errorNameTaken(): String = gson.toJson(ErrorMessage("Данное имя уже занято"))
    fun errorNotFound(): String = gson.toJson(ErrorMessage("Пользователь не найден"))
    fun SuccessName(name: String): String = gson.toJson(SendMessage(name, SERVER))
}
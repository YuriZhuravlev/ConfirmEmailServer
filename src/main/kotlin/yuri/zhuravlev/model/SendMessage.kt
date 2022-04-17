package yuri.zhuravlev.model

import com.google.gson.annotations.SerializedName

data class SendMessage(
    @SerializedName("message")
    val message: String,
    @SerializedName("from")
    val from: String
)

package yuri.zhuravlev.model

import com.google.gson.annotations.SerializedName

data class GetMessage(
    @SerializedName("message")
    val message: String,
    @SerializedName("to")
    val to: String
)
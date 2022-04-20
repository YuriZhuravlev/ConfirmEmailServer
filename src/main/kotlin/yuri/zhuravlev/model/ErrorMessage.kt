package yuri.zhuravlev.model

import com.google.gson.annotations.SerializedName

data class ErrorMessage(
    @SerializedName("error")
    val error: String
)

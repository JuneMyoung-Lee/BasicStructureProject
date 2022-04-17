package com.jmp.basicstructureproject.data.remote.test

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TestResponse(
    @Json(name = "test")
    val test: String?,
    @Json(name = "resultCode")
    val resultCode: Int = 0
)
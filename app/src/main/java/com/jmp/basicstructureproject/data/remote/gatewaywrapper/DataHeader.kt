package com.jmp.basicstructureproject.data.remote.gatewaywrapper

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataHeader(
    val resultCode: String,
    val message: String
)
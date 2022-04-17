package com.jmp.basicstructureproject.data.remote.gatewaywrapper

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GatewayResponse<T>(
    val dataHeader: DataHeader,
    val dataBody: T?
)
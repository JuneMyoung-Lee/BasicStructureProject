package com.jmp.basicstructureproject.data.remote

import com.jmp.basicstructureproject.data.remote.gatewaywrapper.GatewayResponse
import com.jmp.basicstructureproject.data.remote.test.TestResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface RemoteService {
    @POST("post/path")
    fun postTestInfo(
        @Body test: String
    ): Single<GatewayResponse<TestResponse>>

    @GET("get/path")
    fun getTestInfo(
        @Query("test") test: String
    ): Single<GatewayResponse<TestResponse>>
}
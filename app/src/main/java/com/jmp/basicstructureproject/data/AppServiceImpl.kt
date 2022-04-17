package com.jmp.basicstructureproject.data

import com.jmp.basicstructureproject.data.remote.RemoteService
import com.jmp.basicstructureproject.data.remote.gatewaywrapper.GatewayResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AppServiceImpl @Inject constructor(
    private val remoteApi: RemoteService
) : AppService {
    override fun requestPostTest(test: String, callback: Callback<String>) {
        remoteApi.postTestInfo(test)
            .unwrap()
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = {
                    callback.onFailure(it)
                },
                onSuccess = {
                    callback.onSuccess(it.test ?: "")
                }
            )
    }

    override fun requestGetTest(test: String, callback: Callback<String>) {
        remoteApi.getTestInfo(test)
            .unwrap()
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onError = {
                    callback.onFailure(it)
                },
                onSuccess = {
                    callback.onSuccess(it.test ?: "")
                }
            )
    }

    private fun <R> Single<GatewayResponse<R>>.unwrap(): Single<R> {
        return map {
            it.dataBody
        }
    }
}
package com.jmp.basicstructureproject.data.remote

import android.util.Log
import com.jmp.basicstructureproject.data.remote.gatewaywrapper.GatewayResponse
import com.jmp.basicstructureproject.data.remote.test.TestResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

class GatewayErrorCheckRemoteApi @Inject constructor(
    private val origRemoteService: RemoteService,
    private val subject: PublishSubject<String>
) : RemoteService {
    private fun <R, T : GatewayResponse<R>> Single<T>.displayDialogOnGatewayError(): Single<T> {
        return this.doOnSuccess {

            Log.e("ljm", "it.dataHeader.message : ${it.dataHeader.message}")
            if (it.dataHeader.resultCode != "10000") {
                subject.onNext(it.dataHeader.resultCode)
            }
        }
    }

    override fun postTestInfo(test: String): Single<GatewayResponse<TestResponse>> =
        origRemoteService.postTestInfo(test).displayDialogOnGatewayError()

    override fun getTestInfo(test: String): Single<GatewayResponse<TestResponse>> =
        origRemoteService.getTestInfo(test).displayDialogOnGatewayError()


}
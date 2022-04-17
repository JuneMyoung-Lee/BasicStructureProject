package com.jmp.basicstructureproject.presentation

import com.jmp.basicstructureproject.data.AppService
import com.jmp.basicstructureproject.data.Callback
import javax.inject.Inject

class MainRequester @Inject constructor(private val appService: AppService) {
    fun postTestInfo(test: String, callback: Callback<String>) {
        appService.requestPostTest(test, callback)
    }

    fun getTestInfo(test: String, callback: Callback<String>) {
        appService.requestGetTest(test, callback)
    }
}
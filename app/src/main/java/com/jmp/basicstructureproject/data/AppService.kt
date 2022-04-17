package com.jmp.basicstructureproject.data

interface AppService {
    fun requestPostTest(
        test: String,
        callback: Callback<String>
    )

    fun requestGetTest(
        test: String,
        callback: Callback<String>
    )
}
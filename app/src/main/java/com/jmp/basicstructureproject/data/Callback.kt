package com.jmp.basicstructureproject.data

interface Callback<T> {
    fun onSuccess(result: T)

    fun onFailure(e: Throwable)
}
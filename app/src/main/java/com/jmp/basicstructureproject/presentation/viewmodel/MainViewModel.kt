package com.jmp.basicstructureproject.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.jmp.basicstructureproject.data.Callback
import com.jmp.basicstructureproject.data.preferences.PreferencesObject
import com.jmp.basicstructureproject.presentation.MainRequester
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainViewModel @Inject constructor(
    private val preferencesObject : PreferencesObject,
    private val mainRequester: MainRequester
): ViewModel() {

    fun postTestInfo() {
        mainRequester.postTestInfo("test", object: Callback<String> {
            override fun onSuccess(result: String) {

            }

            override fun onFailure(e: Throwable) {

            }
        })
    }

    fun getTestInfo() {
        mainRequester.getTestInfo("test", object: Callback<String> {
            override fun onSuccess(result: String) {

            }

            override fun onFailure(e: Throwable) {

            }
        })
    }
}
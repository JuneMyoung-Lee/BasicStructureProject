package com.jmp.basicstructureproject.presentation.web

import android.app.Activity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.jmp.basicstructureproject.presentation.extension.getActivityContext
import org.json.JSONException
import org.json.JSONObject

class HybridJavaScript(webView: WebView) {
    private val hybridWebView = webView

    /**
     * 플러그인 호출
     */
    @JavascriptInterface
    fun execute(pluginID: String, method: String, json: String?): Boolean {
        // 플러그인은 별도의 쓰레드로 처리한다.
        val jsonObj = json?.let {
            if (json.isNullOrEmpty()) {
                null
            } else {
                try {
                    println("Received $it")
                    JSONObject(it)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    null
                }
            }
        } ?: JSONObject()

        // 메인쓰레드에서 처리가 가능하도록 변경한다.
        val act = hybridWebView.context.getActivityContext() as Activity

        act.runOnUiThread {
            hybridWebView.getHybridPlugInManager().executePlugIn(pluginID, method, jsonObj)
        }

        return true
    }
}
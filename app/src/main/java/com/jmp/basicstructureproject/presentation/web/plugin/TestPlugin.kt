package com.jmp.basicstructureproject.presentation.web.plugin

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.jmp.basicstructureproject.presentation.web.plugin.AppPlugin
import org.json.JSONObject


class TestPlugin : AppPlugin() {

    private lateinit var webView: WebView
    private lateinit var methodName : String

    @JavascriptInterface
    fun testFunction(webview: WebView, json: JSONObject) {
        methodName = Thread.currentThread().stackTrace[2].methodName
        webView = webview


    }
}

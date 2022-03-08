package com.jmp.basicstructureproject.presentation.web.plugin

import org.json.JSONObject

interface HybridWebViewPluginListener {
    fun execute(pluginID: String, method: String, jsonObj: JSONObject) : Boolean
}

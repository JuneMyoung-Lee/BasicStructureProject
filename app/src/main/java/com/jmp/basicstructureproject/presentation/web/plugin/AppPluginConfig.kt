package com.jmp.basicstructureproject.presentation.web.plugin

import com.jmp.basicstructureproject.presentation.web.SetHybridPlugIn

object AppPluginConfig {

    @SetHybridPlugIn(pluginId = "TestPlugin")
    @JvmField val testPlugin = TestPlugin()
}

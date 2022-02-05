package com.jmp.basicstructureproject

import androidx.multidex.MultiDexApplication
import com.jmp.basicstructureproject.composition.AppComponent
import com.jmp.basicstructureproject.composition.DaggerAppComponent

class AppApplication: MultiDexApplication() {

    object CONST {
        const val PROJECT_OWNER = "JuneMyoungLee"
    }

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().bindsApp(this).build()
    }
}
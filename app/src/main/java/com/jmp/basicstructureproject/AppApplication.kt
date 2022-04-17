package com.jmp.basicstructureproject

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.jmp.basicstructureproject.AppApplication.CONST.cookieManager
import com.jmp.basicstructureproject.AppApplication.CONST.cookieStoreName
import com.jmp.basicstructureproject.composition.AppComponent
import com.jmp.basicstructureproject.composition.DaggerAppComponent
import com.jmp.basicstructureproject.presentation.extension.createCookieStore
import net.gotev.cookiestore.WebKitSyncCookieManager
import java.net.CookieManager
import java.net.CookiePolicy

class AppApplication: MultiDexApplication() {

    object CONST {
        const val PROJECT_OWNER = "JuneMyoungLee"

        const val KEY_PREF_ACCESS_TOKEN = "access_token"
        const val KEY_PREF_REFRESH_TOKEN = "refresh_token"

        const val cookieStoreName = "myCookies"

        var baseApiUrl : String = BuildConfig.GRADLE_API_BASE_URL
        var baseWebUrl : String = BuildConfig.GRADLE_WEB_BASE_URL

        lateinit var cookieManager: WebKitSyncCookieManager
    }

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        cookieManager = WebKitSyncCookieManager(
            store = createCookieStore(name = cookieStoreName, persistent = true),
            cookiePolicy = CookiePolicy.ACCEPT_ALL,
            onWebKitCookieManagerError = { exception ->
                // This gets invoked when there's internal webkit cookie manager exceptions
                Log.e("COOKIE-STORE", "WebKitSyncCookieManager error", exception)
            }
        )

        // Setup for HttpURLConnection
        CookieManager.setDefault(cookieManager)

        appComponent = DaggerAppComponent.builder().bindsApp(this).build()
    }
}
package com.jmp.basicstructureproject.composition

import android.content.Context
import android.util.Base64
import com.jmp.basicstructureproject.AppApplication
import com.jmp.basicstructureproject.util.ChCrypto
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AccessTokenInterceptor(private val app: AppApplication) : Interceptor {

    val aesKey = "46a46f91c55bd18012c1524cea735f5e"

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = app.getSharedPreferences(app.packageManager.getPackageInfo(app.packageName, 0)
            .packageName, Context.MODE_PRIVATE).getString(AppApplication.CONST.KEY_PREF_ACCESS_TOKEN, "")

        val newRequest: Request
        val url = chain.request().url.toString()

        if(!url.contains("/land-auth/oauth/token")){
            newRequest = when(token.isNullOrEmpty()){
                true ->{
                    chain.request().newBuilder().build()
                }
                else ->{

                    val timestamp = System.currentTimeMillis()
                    val base64EncodeData = Base64.encodeToString("$token:$timestamp".toByteArray(), Base64.DEFAULT)
                    val aesData = ChCrypto.aesEncrypt(base64EncodeData, aesKey)

                    chain.request().newBuilder()
                        .addHeader("Authorization", "bearer $aesData")
                        .addHeader("timestamp", "$timestamp")
                        .build()
                }
            }
        }else{

            val addHeaderRefreshToken = Base64.encodeToString(
                "localTestId:!QAZ@WSX3e".toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP
            )

            newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Basic $addHeaderRefreshToken")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()
        }

        return chain.proceed(newRequest)
    }
}
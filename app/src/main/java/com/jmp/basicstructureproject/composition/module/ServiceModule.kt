package com.jmp.basicstructureproject.composition.module

import com.jmp.basicstructureproject.AppApplication
import com.jmp.basicstructureproject.AppApplication.CONST.cookieManager
import com.jmp.basicstructureproject.BuildConfig
import com.jmp.basicstructureproject.composition.AccessTokenInterceptor
import com.jmp.basicstructureproject.data.AppService
import com.jmp.basicstructureproject.data.AppServiceImpl
import com.jmp.basicstructureproject.data.remote.GatewayErrorCheckRemoteApi
import com.jmp.basicstructureproject.data.remote.RemoteService
import dagger.Binds
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.subjects.PublishSubject
import net.gotev.cookiestore.okhttp.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
abstract class ServiceModule {
    @Module
    companion object {
        @Provides
        fun providesRetrofit(okHttpClient: OkHttpClient, app: AppApplication): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(AppApplication.CONST.baseApiUrl)
            .build()

        @Provides
        fun providesOkHttpClient(app: AppApplication): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(if(BuildConfig.DEBUG){
                HttpLoggingInterceptor.Level.BODY
            }else{
                HttpLoggingInterceptor.Level.NONE
            })

            return OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(cookieManager))
//                .addInterceptor(AccessTokenInterceptor(app))
                .addInterceptor(logging)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(100, TimeUnit.SECONDS)
                .connectTimeout(100, TimeUnit.SECONDS)
                .build()
        }

        @Provides
        @Named("Orig")
        fun bindsRemoteService(retrofit: Retrofit): RemoteService = retrofit.create(RemoteService::class.java)

        @Provides
        fun providesRemoteService(
            @Named("Orig") origRemote: RemoteService,
            errorSub: PublishSubject<String>
        ): RemoteService = GatewayErrorCheckRemoteApi(
            origRemoteService = origRemote,
            subject = errorSub
        )
    }

    @Binds
    abstract fun bindsAppService(appServiceImpl: AppServiceImpl): AppService
}
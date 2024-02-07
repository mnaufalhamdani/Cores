package com.duakelinci.core.helper

import android.content.Context
import com.duakelinci.core.util.NetworkUtil.isNetworkConnected
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptorImpl(context: Context) :
    ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkConnected(appContext)) throw NoConnectivityException()
        return chain.proceed(chain.request())
    }
}

interface ConnectivityInterceptor : Interceptor
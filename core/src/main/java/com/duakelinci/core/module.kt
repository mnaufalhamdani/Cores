package com.duakelinci.core

import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.duakelinci.core.helper.ConnectivityInterceptorImpl
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

private const val REQUEST_TIMEOUT = 60

fun provideOkHttpClient(context: Context, isDebug: Boolean): OkHttpClient {
    return OkHttpClient.Builder()
        .apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = if (isDebug) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            })
        }
        .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .addInterceptor(ConnectivityInterceptorImpl(context))
        .addNetworkInterceptor(StethoInterceptor())
        .hostnameVerifier(HostnameVerifier { _, _ ->
            return@HostnameVerifier true
        })
        .build()
}

fun provideRetrofit(okHttpClient: OkHttpClient, url: String): Retrofit {
    return Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(url)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideFAN(context: Context) {
    val okHttpClient = OkHttpClient().newBuilder()
        .addNetworkInterceptor(StethoInterceptor())
        .build()
    AndroidNetworking.initialize(context, okHttpClient)
}

fun provideLogging(context: Context, isDebug: Boolean): Timber.Tree {
    return if (isDebug) {
        Stetho.initializeWithDefaults(context)
        DebugTree()
    } else {
        ReleaseTree()
    }
}

//fun provideSplitInstall(context: Context){
//    SplitCompat.install(context)
//}

open class DebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return String.format(
            "Class:%s: Line: %s, Method: %s",
            super.createStackElementTag(element),
            element.lineNumber,
            element.methodName
        )
    }
}

open class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        // log your crash to your favourite
        // Sending crash report to Firebase CrashAnalytics

        // FirebaseCrash.report(message);
        // FirebaseCrash.report(new Exception(message));
    }
}

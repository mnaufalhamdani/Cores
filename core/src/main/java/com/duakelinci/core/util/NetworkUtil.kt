@file:Suppress("DEPRECATION")

package com.duakelinci.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import retrofit2.HttpException

object NetworkUtil {
    fun isHttpStatusCode(throwable: Throwable, statusCode: Int): Boolean {
        return throwable is HttpException && throwable.code() == statusCode
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                return hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
        } else {
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }
        return false
    }
}
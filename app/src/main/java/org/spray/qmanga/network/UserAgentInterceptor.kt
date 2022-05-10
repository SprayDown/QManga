package org.spray.qmanga.network

import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import org.spray.qmanga.BuildConfig
import java.util.*

class UserAgentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        return if (originalRequest.header("User-Agent").isNullOrEmpty()) {
            val newRequest = originalRequest
                .newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64)")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }

    companion object {

        val userAgent
            get() = "QManga/%s (Android %s; %s; %s %s; %s)".format(
                BuildConfig.VERSION_NAME,
                Build.VERSION.RELEASE,
                Build.MODEL,
                Build.BRAND,
                Build.DEVICE,
                Locale.getDefault().language
            )

        val userAgentChrome
            get() = (
                    "Mozilla/5.0 (Linux; Android %s; %s) AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/100.0.4896.127 Mobile Safari/537.36"
                    ).format(
                    Build.VERSION.RELEASE,
                    Build.MODEL,
                )
    }
}
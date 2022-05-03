package org.spray.qmanga.network

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import org.spray.qmanga.QManga
import java.util.concurrent.TimeUnit

class NetworkHelper() {
    companion object {
        private lateinit var okHttpClient: OkHttpClient
        private const val cacheSize = 6L * 1024 * 1024

        fun okHttpClient(): OkHttpClient {
            return okHttpClient
        }

        fun init() {
            okHttpClient = OkHttpClient().newBuilder().apply {
                connectTimeout(20, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
                writeTimeout(20, TimeUnit.SECONDS)
                addInterceptor(CloudFlareInterceptor())
                addInterceptor(UserAgentInterceptor())
                cache(Cache(QManga.appContext.cacheDir, cacheSize))
                cookieJar(AndroidCookieJar())
            }.build()
        }

        fun getJSONObject(url: String): JSONObject? {
            val request = okhttp3.Request.Builder().url(url).build()
            var response: Response? = null
            try {
                response = okHttpClient().newCall(request).execute()
            } catch (e: Exception) {
            }
            return if (response != null) JSONObject(response.body?.string()) else null
        }
    }
}
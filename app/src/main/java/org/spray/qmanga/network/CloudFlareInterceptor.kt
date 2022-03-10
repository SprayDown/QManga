package org.spray.qmanga.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_UNAVAILABLE

class CloudFlareInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == HTTP_FORBIDDEN || response.code == HTTP_UNAVAILABLE) {
            if (response.header(HEADER_SERVER)?.startsWith(SERVER_CLOUDFLARE) == true) {
                response.closeQuietly()
                throw IOException("CloudFlare: " + chain.request().url.toString())
            }
        }
        return response
    }

    private companion object {

        private const val HEADER_SERVER = "Server"
        private const val SERVER_CLOUDFLARE = "cloudflare"
    }
}
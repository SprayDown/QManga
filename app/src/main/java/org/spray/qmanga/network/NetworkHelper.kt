package org.spray.qmanga.network

import android.content.Context
import java.io.File

class NetworkHelper() {

    companion object {
        lateinit var cacheDir: File

        private val cacheSize = 6L * 1024 * 1024

        fun init(context: Context) {
            cacheDir = File(context.cacheDir, "network_cache")
        }


    }
}
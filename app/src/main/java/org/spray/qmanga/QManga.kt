package org.spray.qmanga

import android.app.Application
import android.content.Context
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import org.spray.qmanga.client.source.SourceManager


class QManga : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        try {
            appContext = applicationContext
        } catch (ignored: Throwable) {
        }

        SourceManager.init()
        initImageLoader(applicationContext)
    }

    private fun initImageLoader(context: Context?) {
        val config = ImageLoaderConfiguration.Builder(context).apply {
            threadPriority(Thread.NORM_PRIORITY - 2)
            threadPoolSize(3)
            denyCacheImageMultipleSizesInMemory()
            diskCacheFileNameGenerator(Md5FileNameGenerator())
            diskCacheSize(50 * 1024 * 1024)
            tasksProcessingOrder(QueueProcessingType.LIFO)
            writeDebugLogs()
        }

        ImageLoader.getInstance().init(config.build())
    }
}
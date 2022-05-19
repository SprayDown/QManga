package org.spray.qmanga

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import org.spray.qmanga.client.session.SessionManager
import org.spray.qmanga.client.source.SourceManager
import org.spray.qmanga.network.NetworkHelper

const val NOTIFY_CHANNEL_ID = "qmangaServiceChannel"

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

        NetworkHelper.init()
        SourceManager.init()
        initImageLoader()
        createNotificationChannel()

        SessionManager.auth(SourceManager.getCurrentSource())
    }

    private fun initImageLoader() {
        val config = ImageLoaderConfiguration.Builder(applicationContext).apply {
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFY_CHANNEL_ID,
                "QManga",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            with(channel) {
                setSound(null, null)
                enableLights(false)
                enableVibration(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
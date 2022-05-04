package org.spray.qmanga.ui.impl.library.download

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.spray.qmanga.BuildConfig
import org.spray.qmanga.NOTIFY_CHANNEL_ID
import org.spray.qmanga.R
import org.spray.qmanga.client.local.LocalMangaManager
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.source.SourceManager

class DownloadService : Service() {

    private lateinit var notifyManager: NotificationManagerCompat
    private val builder =
        NotificationCompat.Builder(this, NOTIFY_CHANNEL_ID)

    private val options: DisplayImageOptions = DisplayImageOptions.Builder()
        .showImageOnLoading(null)
        .resetViewBeforeLoading(true)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .considerExifParams(true)
        .build()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        notifyManager = NotificationManagerCompat.from(this)
        val bundle = intent?.getBundleExtra("bundle")
        val mangaData = bundle?.getParcelable<MangaData>("manga_data")

        return if (mangaData != null) {
            start(bundle, mangaData)
            START_REDELIVER_INTENT

        } else START_NOT_STICKY
    }

    private fun start(bundle: Bundle?, mangaData: MangaData) {
        val mangaChapter: MangaChapter? = bundle?.getParcelable("manga_chapter")
        val mangaChapters: ArrayList<MangaChapter>? =
            bundle?.getParcelableArrayList("manga_chapters")

        create(mangaData)
        CoroutineScope(Dispatchers.IO).launch {
            val source = SourceManager.getCurrentSource()
            val localManga =
                withContext(Dispatchers.Default) {
                    LocalMangaManager.loadMangaOrCreate(
                        mangaData,
                        source
                    )
                }

            if (mangaChapter != null) {
                setStatus(
                    DownloadManager.downloadChapter(
                        source,
                        localManga,
                        mangaChapter
                    )
                )
            } else if (mangaChapters != null && mangaChapters.isNotEmpty()) {
                setStatus(
                    DownloadManager.downloadManga(
                        source,
                        localManga,
                        mangaChapters
                    )
                )
            }
        }

        notifyManager.apply {
            builder.setContentTitle("Подготавливаем к загрузке...")
            builder.setProgress(100, 0, true)
            notify(1, builder.build())
        }
    }

    private suspend fun setStatus(flow: Flow<DownloadStatus>): Flow<DownloadStatus> {
        flow.collect {
            withContext(Dispatchers.Main) {
                when (it) {
                    is DownloadStatus.Queued -> {
                        val intent = Intent(CHAPTER_DOWNLOAD_ACTION).apply {
                            putExtra("chapter_id", it.chapter.id)
                            putExtra("queued", true)
                        }
                        sendBroadcast(intent)
                    }
                    is DownloadStatus.Finished -> {
                        with(builder) {
                            setProgress(0, 0, false)
                            setContentTitle("Загрузка завершена")
                            setContentText(it.data.name)
                            setSmallIcon(R.drawable.ic_baseline_check_24)
                            setOngoing(false)
                        }
                        notifyManager.notify(1, builder.build())
                    }
                    is DownloadStatus.Completed -> {
                        val intent = Intent(CHAPTER_DOWNLOAD_ACTION).apply {
                            putExtra("chapter_id", it.chapter.id)
                            putExtra("queued", false)
                        }
                        sendBroadcast(intent)
                    }
                    is DownloadStatus.Error -> {
                        with(builder) {
                            setProgress(0, 0, false)
                            setContentTitle("Ошибка загрузки")
                            setContentText(it.message)
                            setOngoing(false)
                        }
                        notifyManager.notify(1, builder.build())

                        val intent = Intent(CHAPTER_DOWNLOAD_ACTION).apply {
                            putExtra("queued", false)
                        }
                        sendBroadcast(intent)
                    }
                    is DownloadStatus.Progress -> {
                        with(builder) {
                            setOngoing(true)
                            setProgress(100, it.progress, false)
                            setContentText("${it.progress}%")
                        }
                        notifyManager.notify(1, builder.build())
                    }
                }
            }
        }
        return flow
    }

    fun create(data: MangaData) {
        builder.apply {
            setContentTitle(data.name)
            setSmallIcon(android.R.drawable.stat_sys_download)
            setTicker(String())
            priority = NotificationCompat.PRIORITY_LOW

            ImageLoader.getInstance()
                .loadImage(data.imageUrl, options, object : SimpleImageLoadingListener() {
                    override fun onLoadingComplete(
                        imageUri: String?,
                        view: View?,
                        loadedImage: Bitmap?
                    ) {
                        if (loadedImage != null)
                            setLargeIcon(loadedImage)
                    }
                })
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val CHAPTER_DOWNLOAD_ACTION =
            "${BuildConfig.APPLICATION_ID}.action.CHAPTER_DOWNLOADED_ACTION"
    }
}
package org.spray.qmanga.ui.impl.library.download

import android.Manifest
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.spray.qmanga.client.local.LocalMangaManager
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.local.LocalManga
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.network.NetworkHelper
import org.spray.qmanga.sqlite.query.LocalMangaQuery
import org.spray.qmanga.utils.checkPermission
import org.spray.qmanga.utils.ext.await
import java.io.File

class DownloadManager {

    companion object {

        suspend fun downloadManga(
            source: Source,
            data: LocalManga,
            chapters: List<MangaChapter>
        ): Flow<DownloadStatus> = channelFlow {
            val path = LocalMangaManager.getLocalDir() + "${data.eng_name}/"
            try {
                withContext(Dispatchers.Default) {
                    chapters.forEachIndexed { index, chapter ->
                        send(DownloadStatus.Queued(data, chapter))
                        val dir = File(path, "${chapter.tome}-${chapter.number}-${chapter.id}")
                        if (!dir.exists())
                            dir.mkdirs()

                        send(
                            DownloadStatus.Progress(
                                ((index.toDouble() / chapters.size) * 100).toInt()
                            )
                        )

                        withContext(Dispatchers.Default) {
                            var count = 0
                            source.loadPages(chapter).forEach { page ->
                                downloadFile(
                                    page.link,
                                    dir,
                                    "${page.page}-$count"
                                )
                                count++
                            }
                        }

                        send(DownloadStatus.Completed(data, chapter))
                    }
                }
                data.path = path
                send(DownloadStatus.Finished(data, null))
                LocalMangaQuery().createOrUpdate(data, data.hashId, null)
            } catch (e: Exception) {
                send(DownloadStatus.Error(e.message.toString()))
                return@channelFlow
            }
        }

        suspend fun downloadChapter(
            source: Source,
            data: LocalManga,
            chapter: MangaChapter
        ): Flow<DownloadStatus> = channelFlow {
            send(DownloadStatus.Queued(data, chapter))
            val path = LocalMangaManager.getLocalDir() + "${data.eng_name}/"
            val dir = File(path, "${chapter.tome}-${chapter.number}-${chapter.id}")
            if (!dir.exists())
                dir.mkdirs()

            try {
                withContext(Dispatchers.Default) {
                    var count = 0
                    val pages = source.loadPages(chapter)
                    pages.forEach { page ->
                        downloadFile(
                            page.link,
                            dir,
                            "${page.page}-$count"
                        )
                        count++
                        send(
                            DownloadStatus.Progress(
                                ((count.toDouble() / pages.size) * 100).toInt()
                            )
                        )
                    }
                }
                Log.i("qmanga", "download finished")
                data.path = path
                send(DownloadStatus.Completed(data, chapter))
                send(DownloadStatus.Finished(data, chapter))
                LocalMangaQuery().createOrUpdate(data, data.hashId, null)
            } catch (e: Exception) {
                send(DownloadStatus.Error(e.message.toString()))
                return@channelFlow
            }
        }

        private suspend fun downloadFile(
            url: String,
            destination: File,
            tempFileName: String
        ): File {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            val call = NetworkHelper.okHttpClient().newCall(request)
            val file = File(destination, tempFileName)
            val response = call.clone().await()
            runInterruptible(Dispatchers.IO) {
                file.outputStream().use { out ->
                    checkNotNull(response.body).byteStream().copyTo(out)
                }
            }
            return file
        }

        fun checkPermission(activity: FragmentActivity): Boolean {
            return if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
                false
            }
        }
    }
}
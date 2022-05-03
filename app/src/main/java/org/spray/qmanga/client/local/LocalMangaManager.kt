package org.spray.qmanga.client.local

import android.util.Log
import org.spray.qmanga.QManga
import org.spray.qmanga.client.models.MangaChapter
import org.spray.qmanga.client.models.MangaData
import org.spray.qmanga.client.models.MangaDetails
import org.spray.qmanga.client.models.MangaPage
import org.spray.qmanga.client.models.local.LocalManga
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.sqlite.QueryResponse
import org.spray.qmanga.sqlite.query.LocalMangaQuery
import org.spray.qmanga.utils.getRootDirPath
import java.io.File

class LocalMangaManager {

    companion object {

        private val dbQuery = LocalMangaQuery()

        fun getLocalDir(): String {
            return getRootDirPath(QManga.appContext) + "/local/"
        }

        fun parsePages(path: String): List<MangaPage> {
            if (path.isEmpty())
                throw IllegalArgumentException("localManga.path = null")

            val path = File(path)
            val pages = arrayListOf<MangaPage>()
            path.listFiles().forEach {
                pages.add(MangaPage(0, it.absolutePath, it.name.split("-")[0].toInt(), 0, 0))
            }
            return pages
        }

        fun parseChapters(path: String?, hashId: Int): List<MangaChapter>? {
            if (path == null || path.isEmpty())
                return null

            val path = File(path)
            if (!path.exists() || path.listFiles().isEmpty()) {
                LocalMangaQuery().delete(hashId, null)
                return null
            }

            Log.i("qmanga", "start loading chapters")

            val chapters = ArrayList<MangaChapter>()
            path.listFiles().forEach { chapterFile ->
                val split: Array<String?> = chapterFile.name.split("-").toTypedArray()
                chapters.add(
                    MangaChapter(
                        split[2]?.toLongOrNull() ?: 0,
                        String(),
                        split[0]!!.toInt(),
                        split[1] ?: String(),
                    )
                )
            }
            return chapters
        }

        suspend fun loadMangaOrCreate(
            mangaData: MangaData,
            source: Source
        ): LocalManga {
            var localManga = loadManga(mangaData.hashId)
            if (localManga == null) {
                val details = source.loadDetails(mangaData)
                localManga = LocalManga(
                    mangaData.name,
                    mangaData.imageUrl,
                    mangaData.url,
                    mangaData.rating,
                    mangaData.type,
                    eng_name = details.eng_name,
                    description = details.description,
                    avg_rating = details.avg_rating,
                    count_rating = details.count_rating,
                    issue_year = details.issue_year,
                    total_voices = details.total_voices,
                    total_views = details.total_views,
                    previewImgUrl = details.previewImgUrl,
                    status = details.status
                )
            }

            return localManga
        }


        fun loadMangaOrCreate(
            mangaData: MangaData,
            details: MangaDetails?
        ): LocalManga {
            return loadManga(mangaData.hashId) ?: LocalManga(
                mangaData.name,
                mangaData.imageUrl,
                mangaData.url,
                mangaData.rating,
                mangaData.type,
                eng_name = details?.eng_name,
                description = details?.description,
                avg_rating = details?.avg_rating,
                count_rating = details?.count_rating,
                issue_year = details?.issue_year,
                total_voices = details?.total_voices,
                total_views = details?.total_views,
                previewImgUrl = details?.previewImgUrl ?: String(),
                status = details?.status ?: String()
            )
        }

        fun loadManga(hashId: Int): LocalManga? {
            var manga: LocalManga? = null
            dbQuery.read(hashId, object : QueryResponse<LocalManga> {
                override fun onSuccess(data: LocalManga) {
                    manga = data
                }

                override fun onFailure(msg: String) {
                }
            })
            return manga
        }

        fun loadDetails(hashId: Int): MangaDetails? {
            var details: MangaDetails? = null
            dbQuery.read(hashId, object : QueryResponse<LocalManga> {
                override fun onSuccess(data: LocalManga) {
                    details = MangaDetails(
                        data.name,
                        data.type,
                        data.eng_name,
                        data.description,
                        data.avg_rating,
                        data.count_rating,
                        data.issue_year,
                        data.total_views,
                        data.total_voices,
                        data.previewImgUrl,
                        data.status,
                        emptySet(),
                        null,
                        0
                    )
                }

                override fun onFailure(msg: String) {

                }
            })
            return details
        }
    }

}
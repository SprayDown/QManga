package org.spray.qmanga.client.source.impl

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import org.json.JSONException
import org.json.JSONObject
import org.spray.qmanga.client.models.*
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.network.Singleton
import org.spray.qmanga.utils.getJSONObjectOrNull
import org.spray.qmanga.utils.map
import org.spray.qmanga.utils.mapToSet
import java.text.SimpleDateFormat
import java.util.*

class ReMangaSource() : Source() {

    override val domain = "remanga.org"

    private var MAX_COUNT = 20;

    override suspend fun loadPopular(context: Context): List<MangaData> {
        return loadList(context, 1, MAX_COUNT, SortType.POPULAR)
    }

    override suspend fun loadNewest(context: Context): List<MangaData> {
        return loadList(context, 1, MAX_COUNT, SortType.NEWEST)
    }

    override suspend fun loadDetails(context: Context, data: MangaData): MangaDetails {
        val url = "https://api.$domain/api/titles/" + data.url
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val formatter = SimpleDateFormat("dd.MM.yyyy")

        val jsonObject = Singleton.getInstance(context).requestQueue.getJSONObjectOrNull(
            Request.Method.GET,
            url,
            null,
            VolleyError::printStackTrace
        )
        jsonObject ?: throw NullPointerException("JsonObject is null")

        val content = try {
            jsonObject.getJSONObject("content")
        } catch (e: JSONException) {
            throw NullPointerException(jsonObject.optString("msg"))
        }

        val branchId = content.getJSONArray("branches").optJSONObject(0).getLong("id")

        val grabChapters = ArrayList<JSONObject>(100)

        var page = 1
        while (true) {
            val chaptersRequest = Singleton.getInstance(context).requestQueue.getJSONObjectOrNull(
                Request.Method.GET,
                "https://api.$domain/api/titles/chapters/?branch_id=$branchId&page=$page&count=100",
                null,
                VolleyError::printStackTrace
            )
            chaptersRequest ?: break
            val chapters = chaptersRequest.getJSONArray("content")
            val clength = chapters.length()

            if (clength == 0)
                break

            grabChapters.ensureCapacity(grabChapters.size + clength)

            for (i in 0 until clength)
                grabChapters.add(chapters.getJSONObject(i))

            page++
        }

        return MangaDetails(
            data = data,
            description = content.getString("description").replace("<p>", "").replace("</p>", ""),
            avg_rating = content.getString("avg_rating").toFloatOrNull(),
            count_rating = content.getString("count_rating").toIntOrNull(),
            issue_year = content.getInt("issue_year").toString(),
            total_views = content.getString("total_views").toIntOrNull(),
            total_voices = 0,
            status = content.getJSONObject("status").getString("name"),
            previewImgUrl = "https://api.$domain" + content.getJSONObject("img").getString("high"),
            tag = content.getJSONArray("categories").mapToSet { g ->
                MangaTag(g.getString("name"), g.getInt("id"))
            }.plus(content.getJSONArray("genres").mapToSet { g ->
                MangaTag(g.getString("name"), g.getInt("id"))
            }),
            chapters = grabChapters.mapIndexed { index, jsonObject ->
                val publishers = jsonObject.getJSONArray("publishers")
                MangaChapter(
                    id = jsonObject.getLong("id"),
                    name = buildString {
                        append("Глава ")
                        append(jsonObject.optString("chapter", "0"))
                    },
                    tome = jsonObject.getInt("tome"),
                    number = grabChapters.size - index,
                    date = formatter.format(dateFormat.parse(jsonObject.getString("upload_date"))),

                    publisher = if (publishers.length() > 0) publishers.getJSONObject(0)
                        .getString("name") else null
                )
            }.asReversed()
        )
    }

    private suspend fun loadList(
        context: Context, page: Int,
        count: Int,
        sortType: SortType
    ): List<MangaData> {
        val urlBuilder = StringBuilder()
            .append("https://api.")
            .append(domain)
/*        if (query != null) {
            urlBuilder.append("/api/search/?query=")
                .append(query.urlEncoded())
        } else {*/
        urlBuilder.append("/api/search/catalog/?ordering=")
            .append(getSortKey(sortType))
        urlBuilder
            .append("&page=")
            .append(page)
            .append("&count=")
            .append(count)

        val jsonObject = Singleton.getInstance(context).requestQueue.getJSONObjectOrNull(
            Request.Method.GET,
            urlBuilder.toString(),
            null,
            VolleyError::printStackTrace
        )

        jsonObject ?: return emptyList()

        return jsonObject.getJSONArray("content").map { jo ->
            val url = jo.getString("dir")
            val img = jo.getJSONObject("img")

            MangaData(
                name = jo.getString("rus_name"),
                url = url,
                rating = jo.getString("avg_rating").toFloatOrNull()?.div(10f) ?: -1f,
                imageUrl = "https://api.$domain${img.getString("mid")}",
//                largeCoverUrl = "https://api.$domain${img.getString("high")}",
                author = null,
                type = jo.getString("type")
            )
        }
    }

    private fun getSortKey(sortType: SortType?) = when (sortType) {
        SortType.POPULAR -> "-rating"
        SortType.NEWEST -> "-id"
        else -> "-chapter_date"
    }

}
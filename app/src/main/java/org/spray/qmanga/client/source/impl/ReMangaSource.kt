package org.spray.qmanga.client.source.impl

import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.spray.qmanga.client.models.*
import org.spray.qmanga.client.source.Source
import org.spray.qmanga.network.NetworkHelper
import org.spray.qmanga.utils.ext.map
import org.spray.qmanga.utils.ext.mapArray
import org.spray.qmanga.utils.ext.mapToSet
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ReMangaSource() : Source() {

    override val domain = "remanga.org"

    private var MAX_COUNT = 20;

    init {
        sortTypes.addAll(
            listOf(
                SortType.NEWEST,
                SortType.LAST_UPDATES,
                SortType.POPULAR,
                SortType.LIKES,
                SortType.VIEWS,
                SortType.CHAPTER_COUNT,
                SortType.RANDOM
            )
        )

        tags.addAll(
            listOf(
                // Status
                MangaTag("Закончен", 0, TagType.STATUS),
                MangaTag("Продолжается", 1, TagType.STATUS),
                MangaTag("Заморожен", 2, TagType.STATUS),
                MangaTag("Нет переводчика", 3, TagType.STATUS),
                MangaTag("Анонс", 4, TagType.STATUS),
                MangaTag("Лицензировано", 5, TagType.STATUS),

                // Age limit
                MangaTag("Для всех", 0, TagType.LIMIT),
                MangaTag("16+", 1, TagType.LIMIT),
                MangaTag("18+", 2, TagType.LIMIT),

                // Genre
                MangaTag("Боевик", 2, TagType.GENRE),
                MangaTag("Боевые искусства", 3, TagType.GENRE),
                MangaTag("Гарем", 5, TagType.GENRE),
                MangaTag("Гендерная интрига", 6, TagType.GENRE),
                MangaTag("Героическое фэнтези", 7, TagType.GENRE),
                MangaTag("Детектив", 8, TagType.GENRE),
                MangaTag("Дзёсэй", 9, TagType.GENRE),
                MangaTag("Додзинси", 10, TagType.GENRE),
                MangaTag("Драма", 11, TagType.GENRE),
                MangaTag("Игра", 12, TagType.GENRE),
                MangaTag("История", 13, TagType.GENRE),
                MangaTag("Киберпанк", 14, TagType.GENRE),
                MangaTag("Кодомо", 15, TagType.GENRE),
                MangaTag("Комедия", 50, TagType.GENRE),
                MangaTag("Махо-сёдзё", 17, TagType.GENRE),
                MangaTag("Меха", 18, TagType.GENRE),
                MangaTag("Мистика", 19, TagType.GENRE),
                MangaTag("Научная фантастика", 20, TagType.GENRE),
                MangaTag("Повседневность", 21, TagType.GENRE),
                MangaTag("Постапокалиптика", 22, TagType.GENRE),
                MangaTag("Приключения", 23, TagType.GENRE),
                MangaTag("Психология", 24, TagType.GENRE),
                MangaTag("Романтика", 25, TagType.GENRE),
                MangaTag("Сверхъестественное", 27, TagType.GENRE),
                MangaTag("Сёдзё", 28, TagType.GENRE),
                MangaTag("Сёдзё-ай", 29, TagType.GENRE),
                MangaTag("Сёнен", 30, TagType.GENRE),
                MangaTag("Сёнен-ай", 31, TagType.GENRE),
                MangaTag("Спорт", 32, TagType.GENRE),
                MangaTag("Сэйнен", 33, TagType.GENRE),
                MangaTag("Трагедия", 34, TagType.GENRE),
                MangaTag("Триллер", 35, TagType.GENRE),
                MangaTag("Ужасы", 36, TagType.GENRE),
                MangaTag("Фантастика", 37, TagType.GENRE),
                MangaTag("Фэнтези", 38, TagType.GENRE),
                MangaTag("Школа", 39, TagType.GENRE),
                MangaTag("Элементы юмора", 16, TagType.GENRE),
                MangaTag("Эротика", 42, TagType.GENRE),
                MangaTag("Этти", 40, TagType.GENRE),
                MangaTag("Юри", 41, TagType.GENRE),
                MangaTag("Яой", 43, TagType.GENRE),

                // Category
                MangaTag("Алхимия", 47),
                MangaTag("Амнезия / Потеря памяти", 121),
                MangaTag("Ангелы", 48),
                MangaTag("Антигерой", 26),
                MangaTag("Антиутопия", 49),
                MangaTag("Апокалипсис", 50),
                MangaTag("Аристократия", 117),
                MangaTag("Артефакты", 52),
                MangaTag("Боги", 45),
                MangaTag("Бои на мечях", 122),
                MangaTag("Борьба за власть", 54),
                MangaTag("Будущее", 55),
                MangaTag("В цвете", 6),
                MangaTag("Вампиры", 112),
                MangaTag("Вестерн", 56),
                MangaTag("Видеоигры", 35),
                MangaTag("Виртуальная реальность", 44),
                MangaTag("Владыка демонов", 57),
                MangaTag("Военные", 29),
                MangaTag("Волшебные существа", 59),
                MangaTag("Воспоминания из другого мира", 60),
                MangaTag("Врачи / доктора", 116),
                MangaTag("Выживание", 41),
                MangaTag("ГГ женщина", 63),
                MangaTag("ГГ имба", 110),
                MangaTag("ГГ мужчина", 64),
                MangaTag("ГГ не человек", 123),
                MangaTag("Геймеры", 61),
                MangaTag("Гильдии", 62),
                MangaTag("Гоблины", 65),
                MangaTag("Горничные", 23),
                MangaTag("Грузовик-сан", 125),
                MangaTag("Гяру", 28),
                MangaTag("Девушки-монстры", 37),
                MangaTag("Демоны", 15),
                MangaTag("Драконы", 66),
                MangaTag("Дружбы", 67),
                MangaTag("Ёнкома", 8),
                MangaTag("Жестокий мир", 69),
                MangaTag("Животные компаньоны", 70),
                MangaTag("Завоевание мира", 71),
                MangaTag("Зверолюди", 19),
                MangaTag("Зомби", 14),
                MangaTag("Игровые элементы", 73),
                MangaTag("Исекай", 115),
                MangaTag("Квесты", 75),
                MangaTag("Космос", 76),
                MangaTag("Кулинария", 16),
                MangaTag("Культивация", 18),
                MangaTag("Лоли", 108),
                MangaTag("Магическая академия", 78),
                MangaTag("Магия", 22),
                MangaTag("Мафия", 24),
                MangaTag("Медицина", 17),
                MangaTag("Месть", 79),
                MangaTag("Монстры", 38),
                MangaTag("Музыка", 39),
                MangaTag("Навыки / способности", 80),
                MangaTag("Наёмники", 81),
                MangaTag("Насилие / жестокость", 82),
                MangaTag("Нежить", 83),
                MangaTag("Ниндзя", 30),
                MangaTag("Оборотни", 113),
                MangaTag("Обратный гарем", 40),
                MangaTag("Офисные работники", 31),
                MangaTag("Пародия", 85),
                MangaTag("Подземелья", 86),
                MangaTag("Политика", 87),
                MangaTag("Полиция", 32),
                MangaTag("Преступники / Криминал", 36),
                MangaTag("Призраки / Духи", 27),
                MangaTag("Прокачка", 118),
                MangaTag("Психодел-упоротость-Треш", 124),
                MangaTag("Путешествие во времени", 43),
                MangaTag("Разумные расы", 88),
                MangaTag("Ранги силы", 68),
                MangaTag("Реинкарнация", 13),
                MangaTag("Роботы", 89),
                MangaTag("Рыцари", 90),
                MangaTag("Самураи", 33),
                MangaTag("Сборник", 10),
                MangaTag("Сингл", 11),
                MangaTag("Система", 91),
                MangaTag("Скрытие личности", 93),
                MangaTag("Спасение мира", 94),
                MangaTag("Средневековье", 25),
                MangaTag("Стимпанк", 92),
                MangaTag("Супергерои", 95),
                MangaTag("Традиционные игры", 34),
                MangaTag("Тупой ГГ", 109),
                MangaTag("Умный ГГ", 111),
                MangaTag("Управление территорией", 114),
                MangaTag("Учитель / Ученик", 96),
                MangaTag("Философия", 97),
                MangaTag("Хентай", 12),
                MangaTag("Хикикомори", 21),
                MangaTag("Шантаж", 99),
                MangaTag("Эльфы", 46)
            )
        )
    }

    override suspend fun loadPopular(): List<MangaData> {
        return loadList(1, MAX_COUNT, null, SortType.POPULAR)
    }

    override suspend fun loadNewest(): List<MangaData> {
        return loadList(1, MAX_COUNT, null, SortType.NEWEST)
    }

    override suspend fun loadDetails(data: MangaData): MangaDetails {
        val url = "https://api.$domain/api/titles/" + data.url
        val jsonObject = NetworkHelper.getJSONObject(url)
        jsonObject ?: throw NullPointerException("JsonObject is null")

        val content = try {
            jsonObject.getJSONObject("content")
        } catch (e: JSONException) {
            throw NullPointerException(jsonObject.optString("msg"))
        }

        return MangaDetails(
            name = data.name,
            type = data.type,
            eng_name = content.getString("en_name"),
            description = Jsoup.parse(content.getString("description")).text(),
            avg_rating = content.getString("avg_rating").toFloatOrNull(),
            count_rating = content.getString("count_rating").toIntOrNull(),
            issue_year = content.getInt("issue_year").toString(),
            total_views = content.getString("total_views").toIntOrNull(),
            total_voices = 0,
            status = content.getJSONObject("status").getString("name"),
            previewImgUrl = "https://api.$domain" + content.getJSONObject("img").getString("high"),
            tag = content.getJSONArray("genres").mapToSet { g ->
                MangaTag(g.getString("name"), g.getInt("id"))
            },
            branchId = content.getJSONArray("branches").optJSONObject(0).getLong("id"),
            count_chapters = content.getInt("count_chapters")
        )
    }

    override suspend fun loadChapters(
        branchId: Long
    ): List<MangaChapter> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val formatter = SimpleDateFormat("dd.MM.yyyy")

        val grabChapters = ArrayList<JSONObject>(100)

        var page = 1
        while (true) {
            val chaptersRequest =
                NetworkHelper.getJSONObject("https://api.$domain/api/titles/chapters/?branch_id=${branchId}&page=$page&count=100")
            chaptersRequest ?: break
            val chapters = chaptersRequest.getJSONArray("content")
            val length = chapters.length()

            if (length == 0)
                break

            grabChapters.ensureCapacity(grabChapters.size + length)

            for (i in 0 until length)
                grabChapters.add(chapters.getJSONObject(i))

            page++
        }

        return grabChapters.mapIndexed { _, jsonObject ->
            val publishers = jsonObject.getJSONArray("publishers")
            var publishDate: String? = null
            try {
                publishDate = formatter.format(dateFormat.parse(jsonObject.getString("pub_date")))
            } catch (ignored: Exception) {
            }

            MangaChapter(
                id = jsonObject.getLong("id"),
                name = jsonObject.getString("name"),
                tome = jsonObject.getInt("tome"),
                number = jsonObject.getString("chapter"),
                date = formatter.format(dateFormat.parse(jsonObject.getString("upload_date"))),

                publisher = if (publishers.length() > 0) publishers.getJSONObject(0)
                    .getString("name") else null,
                locked = jsonObject.getBoolean("is_paid"),
                pub_date = publishDate
            )
        }.asReversed()
    }

    override suspend fun loadPages(chapter: MangaChapter): List<MangaPage> {
        val jsonObject =
            NetworkHelper.getJSONObject("https://api.$domain/api/titles/chapters/" + chapter.id)
        jsonObject ?: throw NullPointerException("JsonObject is null")

        val list = arrayListOf<MangaPage>()
        val pageArray = jsonObject.getJSONObject("content").getJSONArray("pages")

        //        for (i in 0 until pageArray.length()) {
        pageArray.mapArray { js ->
            list.add(
                MangaPage(
                    id = js.getLong("id"),
                    link = js.getString("link"),
                    page = js.getInt("page"),
                    width = js.getInt("width"),
                    height = js.getInt("height")
                )
            )
//            }
        }
        return list
    }

    override suspend fun search(query: String): List<MangaData> {
        return loadList(0, 20, query, null);
    }

    private suspend fun loadList(
        page: Int,
        count: Int, query: String?,
        sortType: SortType?
    ): List<MangaData> {
        return loadList(page, count, QueryData(query), sortType, ListType.DESCENDING)
    }

    override suspend fun loadList(
        page: Int,
        count: Int,
        queryData: QueryData,
        sortType: SortType?,
        listType: ListType
    ): List<MangaData> {
        val urlBuilder = StringBuilder()
            .append("https://api.")
            .append(domain)
        if (queryData.query != null) {
            urlBuilder.append("/api/search/?query=")
                .append(URLEncoder.encode(queryData.query))
        } else {
            val sortAppend = if (listType == ListType.DESCENDING) "-" else String()
            urlBuilder.append("/api/search/catalog/?ordering=")
                .append(sortAppend + getSortKey(sortType))
            urlBuilder
                .append("&page=")
                .append(page)
                .append("&count=")
                .append(count)

            if (queryData.status != null && queryData.status.isNotEmpty())
                queryData.status.forEach { status ->
                    urlBuilder.append("&status=${status.id}")
                }

            if (queryData.limit != null && queryData.limit.isNotEmpty())
                queryData.limit.forEach { limit ->
                    urlBuilder.append("&age_limit=${limit.id}")
                }

            if (queryData.genres != null && queryData.genres.isNotEmpty())
                queryData.genres.forEach { genre ->
                    urlBuilder.append("&genres=${genre.id}")
                }

            if (queryData.categories != null && queryData.categories.isNotEmpty())
                queryData.categories.forEach { category ->
                    urlBuilder.append("&categories=${category.id}")
                }
        }

        val jsonObject = NetworkHelper.getJSONObject(urlBuilder.toString())
        jsonObject ?: return emptyList()

        return jsonObject.getJSONArray("content").map { jo ->
            val url = jo.getString("dir")
            val img = jo.getJSONObject("img")

            val imageUrl = jo.getString("cover_mid") ?: img.getString("low")
            val media = if (imageUrl != null) "/media/" else String()

            MangaData(
                name = jo.getString("rus_name"),
                url = url,
                rating = jo.getString("avg_rating"),
                imageUrl = "https://api.$domain$media$imageUrl",
//                largeCoverUrl = "https://api.$domain${img.getString("high")}"
                type = jo.getString("type")
            )
        }
    }

    private fun getSortKey(sortType: SortType?) = when (sortType) {
        SortType.POPULAR -> "rating"
        SortType.NEWEST -> "id"
        SortType.LAST_UPDATES -> "chapter_date"
        SortType.VIEWS -> "views"
        SortType.LIKES -> "votes"
        SortType.CHAPTER_COUNT -> "count_chapters"
        else -> "random"
    }

}
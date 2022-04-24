package org.spray.qmanga.client.models

import android.os.Parcel
import android.os.Parcelable

open class MangaData(
    val name: String,
    val imageUrl: String,
    val url: String,
    val rating: String,
    val type: String,
    var hashId: Int = url.hashCode()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(imageUrl)
        parcel.writeString(url)
        parcel.writeString(rating)
        parcel.writeString(type)
        parcel.writeInt(hashId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MangaData> {
        override fun createFromParcel(parcel: Parcel): MangaData {
            return MangaData(parcel)
        }

        override fun newArray(size: Int): Array<MangaData?> {
            return arrayOfNulls(size)
        }
    }
}

data class MangaDetails(
    val data: MangaData,
    var eng_name: String? = null,
    val description: String?,
    val avg_rating: Float?,
    val count_rating: Int?,
    val issue_year: String?,
    val total_views: Int?,
    val total_voices: Int?,
    val previewImgUrl: String,
    val status: String,
    val tag: Set<MangaTag>,
    var author: String? = null,
    val branchId: Long,
    var count_chapters: Int = 0
)

data class MangaChapter(
    val id: Long,
    val name: String,
    val tome: Int,
    val number: String,
    val date: String?,
    val publisher: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeInt(tome)
        parcel.writeString(number)
        parcel.writeString(date)
        parcel.writeString(publisher)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MangaChapter> {
        override fun createFromParcel(parcel: Parcel): MangaChapter {
            return MangaChapter(parcel)
        }

        override fun newArray(size: Int): Array<MangaChapter?> {
            return arrayOfNulls(size)
        }
    }
}

data class MangaPage(val id: Long, val link: String, val page: Int, val width: Int, val height: Int)

data class MangaTag(val name: String, val id: Int, val type: TagType? = TagType.CATEGORY) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        TagType.valueOf(parcel.readString().toString())
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(id)
        parcel.writeString(type?.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MangaTag> {
        override fun createFromParcel(parcel: Parcel): MangaTag {
            return MangaTag(parcel)
        }

        override fun newArray(size: Int): Array<MangaTag?> {
            return arrayOfNulls(size)
        }
    }
}
package org.spray.qmanga.client.models

import android.os.Parcel
import android.os.Parcelable

open class MangaChapter(
    val id: Long = 0,
    val name: String,
    val tome: Int,
    val number: String,
    val date: String? = null,
    val publisher: String? = null,
    val locked: Boolean = false,
    var local: Boolean = false,
    val pub_date: String? = null
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

    fun equalsChapter(chapter: MangaChapter): Boolean {
        return tome == chapter.tome && number == chapter.number
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
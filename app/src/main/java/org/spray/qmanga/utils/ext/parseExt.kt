package org.spray.qmanga.utils.ext

import androidx.collection.ArraySet
import org.json.JSONArray
import org.json.JSONObject

inline fun <R, C : MutableCollection<in R>> JSONArray.mapTo(
    destination: C,
    block: (JSONObject) -> R
): C {
    val len = length()
    for (i in 0 until len) {
        val jo = getJSONObject(i)
        destination.add(block(jo))
    }
    return destination
}

inline fun <R, C : MutableCollection<in R>> JSONArray.mapArrayTo(
    destination: C,
    block: (JSONObject) -> R
): C {
    val len = length()
    for (i in 0 until len) {
        if (get(i) is JSONArray) {
            val array = getJSONArray(i)
            for (i in 0 until array.length()) {
                val jo = array.getJSONObject(i)
                destination.add(block(jo))
            }
        } else {
            val jo = getJSONObject(i)
            destination.add(block(jo))
        }
    }
    return destination
}

inline fun <T> JSONArray.mapArray(block: (JSONObject) -> T): List<T> {
    return mapArrayTo(ArrayList(length()), block)
}

inline fun <T> JSONArray.map(block: (JSONObject) -> T): List<T> {
    return mapTo(ArrayList(length()), block)
}

fun <T> JSONArray.mapToSet(block: (JSONObject) -> T): Set<T> {
    val len = length()
    val result = ArraySet<T>(len)
    for (i in 0 until len) {
        val jo = getJSONObject(i)
        result.add(block(jo))
    }
    return result
}



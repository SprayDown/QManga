package org.spray.qmanga.utils.ext

import androidx.collection.ArraySet
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Response
import okhttp3.internal.closeQuietly
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume

fun Response.parseJson(): JSONObject {
    try {
        val string = body?.string() ?: throw NullPointerException("Response body is null")
        return JSONObject(string)
    } finally {
        closeQuietly()
    }
}

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

suspend inline fun RequestQueue.getJSONObjectOrNull(
    method: Int,
    url: String,
    jsonRequest: JSONObject?,
    crossinline onError: (VolleyError) -> Unit = {}
): JSONObject? = suspendCancellableCoroutine { continuation ->
    val request = JsonObjectRequest(
        method,
        url,
        jsonRequest,
        { result: JSONObject -> continuation.resume(result) },
        { error ->
            onError(error)
            continuation.resume(null)
        }
    )
    add(request)
    continuation.invokeOnCancellation { request.cancel() }
}

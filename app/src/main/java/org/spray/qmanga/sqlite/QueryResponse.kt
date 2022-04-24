package org.spray.qmanga.sqlite

interface QueryResponse<T> {
    fun onSuccess(data: T)
    fun onFailure(msg: String)
}
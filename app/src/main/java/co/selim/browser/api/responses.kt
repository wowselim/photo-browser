package co.selim.browser.api

import retrofit2.Response

val <T> Response<T>.body: T
    get() = checkNotNull(body())

sealed interface NetworkResource<out T> {
    data class Loaded<T>(val value: T) : NetworkResource<T>
    object Loading : NetworkResource<Nothing>
    object Error : NetworkResource<Nothing>
}

package co.selim.browser.api

import retrofit2.Response

val <T> Response<T>.body: T
    get() = checkNotNull(body())

sealed interface NetworkResource<out T> {
    data class Loaded<T>(val value: T) : NetworkResource<T>
    object Loading : NetworkResource<Nothing>
    object Error : NetworkResource<Nothing>
}


suspend fun <T> mapResponse(block: suspend () -> Response<T>): NetworkResource<T> {
    return try {
        val response = block()

        if(response.isSuccessful) {
            NetworkResource.Loaded(response.body)
        } else {
            NetworkResource.Error
        }
    } catch (t: Throwable) {
        NetworkResource.Error
    }
}

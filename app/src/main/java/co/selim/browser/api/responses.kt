package co.selim.browser.api

import retrofit2.Response

val <T> Response<T>.body: T
    get() = checkNotNull(body())

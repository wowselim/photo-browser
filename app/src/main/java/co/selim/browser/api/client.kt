package co.selim.browser.api

import co.selim.browser.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDateTime

private const val apiKeyHeaderName = "X-API-KEY"

private val httpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                    .apply {
                        redactHeader(apiKeyHeaderName)
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                addInterceptor(loggingInterceptor)
            }
        }
        .addInterceptor { chain ->
            val authenticatedRequest = chain.request()
                .newBuilder()
                .header(apiKeyHeaderName, BuildConfig.apiKey)
                .build()

            chain.proceed(authenticatedRequest)
        }
        .build()
}

private val moshi: Moshi by lazy {
    Moshi.Builder()
        .add(LocalDateTime::class.java, LocalDateTimeAdapter)
        .build()
}

private val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl("https://selim.co/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(httpClient)
        .build()
}

val apiClient: SelimCoClient by lazy {
    retrofit.create(SelimCoClient::class.java)
}

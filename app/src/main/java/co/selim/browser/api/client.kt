package co.selim.browser.api

import co.selim.browser.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDateTime

private const val apiKeyHeaderName = "X-API-KEY"

val apiModule = module {
    single {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
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
            .cache(Cache(androidContext().cacheDir, 1024 * 10))
            .build()

        val moshi: Moshi = Moshi.Builder()
            .add(LocalDateTime::class.java, LocalDateTimeAdapter)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://selim.co/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(httpClient)
            .build()

        retrofit.create(SelimCoClient::class.java)
    }
}

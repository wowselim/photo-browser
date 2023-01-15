package co.selim.browser.api

import co.selim.browser.model.Album
import co.selim.browser.model.AlbumPage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SelimCoClient {

    @GET("/api/albums")
    suspend fun getAllAlbums(): Response<AlbumPage>

    @GET("/api/albums/{slug}")
    suspend fun getAlbumBySlug(@Path("slug") slug: String): Response<Album>
}
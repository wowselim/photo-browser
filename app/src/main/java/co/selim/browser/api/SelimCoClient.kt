package co.selim.browser.api

import co.selim.browser.model.Album
import co.selim.browser.model.AlbumPage
import co.selim.browser.model.Photo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SelimCoClient {

    @GET("/api/albums")
    suspend fun getAllAlbums(@Query("page") page: Int): Response<AlbumPage>

    @GET("/api/albums/{slug}")
    suspend fun getAlbumBySlug(@Path("slug") slug: String): Response<Album>

    @GET("/api/photos/{uri}")
    suspend fun getPhotoByUri(@Path("uri") uri: String): Response<Photo>
}
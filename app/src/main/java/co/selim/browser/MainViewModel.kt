package co.selim.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.selim.browser.api.NetworkResource
import co.selim.browser.api.apiClient
import co.selim.browser.api.body
import co.selim.browser.model.Album
import co.selim.browser.paging.AlbumPagingSource
import kotlinx.coroutines.flow.Flow

class MainViewModel : ViewModel() {

    private val inMemoryAlbumCache = mutableMapOf<String, Album>()

    suspend fun loadAlbum(slug: String): NetworkResource<Album> {
        val existingAlbum = inMemoryAlbumCache[slug]
        if (existingAlbum != null) {
            return NetworkResource.Loaded(existingAlbum)
        }

        return try {
            val response = apiClient.getAlbumBySlug(slug)

            if (response.isSuccessful) {
                val album = response.body
                inMemoryAlbumCache[slug] = album
                NetworkResource.Loaded(album)
            } else {
                NetworkResource.Error
            }
        } catch (t: Throwable) {
            NetworkResource.Error
        }
    }

    val albums: Flow<PagingData<Album>> = Pager(
        config = PagingConfig(pageSize = 8),
        pagingSourceFactory = { AlbumPagingSource() }
    ).flow.cachedIn(viewModelScope)
}

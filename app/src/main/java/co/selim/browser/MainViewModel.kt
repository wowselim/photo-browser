package co.selim.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.selim.browser.api.NetworkResource
import co.selim.browser.api.SelimCoClient
import co.selim.browser.api.mapResponse
import co.selim.browser.model.Album
import co.selim.browser.model.Photo
import co.selim.browser.paging.AlbumPagingSource
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : ViewModel(), KoinComponent {

    private val apiClient: SelimCoClient by inject()
    private val inMemoryAlbumCache = mutableMapOf<String, Album>()
    private val inMemoryPhotoCache = mutableMapOf<String, Photo>()

    suspend fun loadAlbum(slug: String): NetworkResource<Album> {
        val existingAlbum = inMemoryAlbumCache[slug]
        if (existingAlbum != null) {
            return NetworkResource.Loaded(existingAlbum)
        }

        val resource = mapResponse { apiClient.getAlbumBySlug(slug) }
        if (resource is NetworkResource.Loaded) {
            inMemoryAlbumCache[slug] = resource.value
        }
        return resource
    }

    suspend fun loadPhoto(uri: String): NetworkResource<Photo> {
        val existingPhoto = inMemoryPhotoCache[uri]
        if (existingPhoto != null) {
            return NetworkResource.Loaded(existingPhoto)
        }

        val resource = mapResponse { apiClient.getPhotoByUri(uri) }
        if (resource is NetworkResource.Loaded) {
            inMemoryPhotoCache[uri] = resource.value
        }
        return resource
    }

    val albums: Flow<PagingData<Album>> = Pager(
        config = PagingConfig(pageSize = 8),
        pagingSourceFactory = { AlbumPagingSource(apiClient) }
    ).flow.cachedIn(viewModelScope)
}

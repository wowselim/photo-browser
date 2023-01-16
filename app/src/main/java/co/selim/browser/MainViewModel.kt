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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : ViewModel(), KoinComponent {

    private val apiClient: SelimCoClient by inject()
    private val inMemoryAlbumCache = mutableMapOf<String, Album>()
    private val inMemoryPhotoCache = mutableMapOf<String, Photo>()

    private val _photoFlow = MutableStateFlow<NetworkResource<Photo>>(NetworkResource.Loading)
    val photoFlow: StateFlow<NetworkResource<Photo>> = _photoFlow

    private val _albumFlow = MutableStateFlow<NetworkResource<Album>>(NetworkResource.Loading)
    val albumFlow: StateFlow<NetworkResource<Album>> = _albumFlow

    val albums: Flow<PagingData<Album>> = Pager(
        config = PagingConfig(pageSize = 8),
        pagingSourceFactory = { AlbumPagingSource(apiClient) }
    ).flow.cachedIn(viewModelScope)

    fun loadAlbum(slug: String) {

        val existingAlbum = inMemoryAlbumCache[slug]
        if (existingAlbum != null) {
            _albumFlow.value = NetworkResource.Loaded(existingAlbum)
        }

        viewModelScope.launch {
            val resource = mapResponse { apiClient.getAlbumBySlug(slug) }
            if (resource is NetworkResource.Loaded) {
                inMemoryAlbumCache[slug] = resource.value
            }
            _albumFlow.value = resource
        }
    }

    fun loadPhoto(uri: String) {
        val existingPhoto = inMemoryPhotoCache[uri]
        if (existingPhoto != null) {
            _photoFlow.value = NetworkResource.Loaded(existingPhoto)
        }

        viewModelScope.launch {
            val resource = mapResponse { apiClient.getPhotoByUri(uri) }
            if (resource is NetworkResource.Loaded) {
                inMemoryPhotoCache[uri] = resource.value
            }
            _photoFlow.value = resource
        }
    }
}

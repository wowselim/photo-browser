package co.selim.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.selim.browser.api.apiClient
import co.selim.browser.model.Album
import co.selim.browser.model.AlbumPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _album = MutableStateFlow<NetworkResource<Album>>(NetworkResource.Loading)
    val album: StateFlow<NetworkResource<Album>> = _album

    private val _pages = MutableStateFlow<NetworkResource<List<AlbumPage>>>(NetworkResource.Loading)
    val pages: StateFlow<NetworkResource<List<AlbumPage>>> = _pages

    sealed interface NetworkResource<out T> {
        data class Loaded<T>(val value: T) : NetworkResource<T>
        object Loading : NetworkResource<Nothing>
        object Error : NetworkResource<Nothing>
    }

    fun loadWildlifeAlbum() {
        viewModelScope.launch {
            val albumBySlug = apiClient.getAlbumBySlug("wildlife")
            if (albumBySlug.isSuccessful) {
                _album.emit(NetworkResource.Loaded(albumBySlug.body()!!))
            } else {
                _album.emit(NetworkResource.Error)
            }
        }
    }
}

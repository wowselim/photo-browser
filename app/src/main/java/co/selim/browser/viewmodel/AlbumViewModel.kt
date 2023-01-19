package co.selim.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.selim.browser.api.NetworkResource
import co.selim.browser.api.SelimCoClient
import co.selim.browser.api.mapResponse
import co.selim.browser.model.Album
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlbumViewModel(private val albumSlug: String) : ViewModel(), KoinComponent {

    private val apiClient: SelimCoClient by inject()

    private val _albumFlow = MutableStateFlow<NetworkResource<Album>>(NetworkResource.Loading)
    val albumFlow: StateFlow<NetworkResource<Album>> = _albumFlow

    init {
        viewModelScope.launch {
            val resource = mapResponse { apiClient.getAlbumBySlug(albumSlug) }
            _albumFlow.value = resource
        }
    }
}
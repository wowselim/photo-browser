package co.selim.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.selim.browser.api.NetworkResource
import co.selim.browser.api.SelimCoClient
import co.selim.browser.api.mapResponse
import co.selim.browser.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PhotoViewModel(private val photoUri: String) : ViewModel(), KoinComponent {

    private val apiClient: SelimCoClient by inject()

    private val _photoFlow = MutableStateFlow<NetworkResource<Photo>>(NetworkResource.Loading)
    val photoFlow: StateFlow<NetworkResource<Photo>> = _photoFlow

    init {
        viewModelScope.launch {
            val resource = mapResponse { apiClient.getPhotoByUri(photoUri) }
            _photoFlow.value = resource
        }
    }
}
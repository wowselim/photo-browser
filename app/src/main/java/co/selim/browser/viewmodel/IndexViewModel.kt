package co.selim.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.selim.browser.api.SelimCoClient
import co.selim.browser.model.Album
import co.selim.browser.paging.AlbumPagingSource
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IndexViewModel : ViewModel(), KoinComponent {

    private val apiClient: SelimCoClient by inject()

    val albums: Flow<PagingData<Album>> = Pager(
        config = PagingConfig(pageSize = 8),
        pagingSourceFactory = { AlbumPagingSource(apiClient) }
    ).flow.cachedIn(viewModelScope)
}

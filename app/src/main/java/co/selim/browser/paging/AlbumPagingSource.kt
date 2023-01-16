package co.selim.browser.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import co.selim.browser.api.apiClient
import co.selim.browser.api.body
import co.selim.browser.model.Album

class AlbumPagingSource : PagingSource<Int, Album>() {

    override fun getRefreshKey(state: PagingState<Int, Album>): Int {
        return ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2).coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        return try {
            val page = params.key ?: 0
            val response = apiClient.getAllAlbums(page)

            LoadResult.Page(
                data = response.body.albums,
                prevKey = response.body.previous,
                nextKey = response.body.next,
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }
}
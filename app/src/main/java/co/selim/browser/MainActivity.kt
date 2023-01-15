package co.selim.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.selim.browser.model.Album
import co.selim.browser.ui.theme.BrowserTheme
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrowserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LazyColumn {
                        item {
                            AlbumTitleItem(albumFlow = viewModel.album)
                            viewModel.loadWildlifeAlbum()
                        }

                        item {
                            Text(text = BuildConfig.apiKey)
                        }

                        item {
                            Greeting("Android")
                        }

                        item {
                            Text(text = "Item 1")
                        }

                        items(3) { i ->
                            Text(text = "Item ${i + 1}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumTitleItem(albumFlow: StateFlow<MainViewModel.NetworkResource<Album>>) {
    val state = albumFlow.collectAsState()
    when (val resource = state.value) {
        MainViewModel.NetworkResource.Error -> Text(text = "Failed to load album")
        is MainViewModel.NetworkResource.Loaded -> Text(text = resource.value.title)
        MainViewModel.NetworkResource.Loading -> Text(text = "Loading…")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BrowserTheme {
        Greeting("Android")
    }
}

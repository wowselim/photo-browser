package co.selim.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import co.selim.browser.api.NetworkResource
import co.selim.browser.model.Album
import co.selim.browser.ui.theme.BrowserTheme
import co.selim.browser.ui.utils.thumbnailSrc
import coil.compose.AsyncImage

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
                    val albums = viewModel.albums.collectAsLazyPagingItems()

                    val navController = rememberNavController()

                    Column {
                        Text(text = "selim.co", Modifier.padding(24.dp), fontSize = 24.sp)

                        NavHost(navController = navController, startDestination = "albums") {
                            composable("albums") {
                                Column(content = albumsView(albums, navController))
                            }

                            composable(
                                "album/{slug}",
                                arguments = listOf(navArgument("slug") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val slug = backStackEntry.arguments?.getString("slug")!!
                                val (album, setAlbum) = remember {
                                    mutableStateOf<NetworkResource<Album>>(
                                        NetworkResource.Loading
                                    )
                                }
                                LaunchedEffect(key1 = slug) {
                                    setAlbum(viewModel.loadAlbum(slug))
                                }

                                when (album) {
                                    NetworkResource.Error -> Text(text = "Failed to load photos")
                                    is NetworkResource.Loaded -> Column(content = albumView(album.value))
                                    NetworkResource.Loading -> Text(text = "Loading…")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun albumsView(
    albums: LazyPagingItems<Album>,
    navController: NavHostController
): @Composable (ColumnScope.() -> Unit) =
    {
        Text(text = "Albums", Modifier.padding(24.dp), fontSize = 18.sp)

        LazyColumn {
            items(items = albums, key = { it.id }) { album ->
                if (album != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { navController.navigate("album/${album.slug}") }) {
                        AsyncImage(
                            model = album.coverPhoto.thumbnailSrc,
                            contentDescription = "${album.title} cover photo",
                            modifier = Modifier.fillMaxSize(),
                        )
                        TextWithShadow(
                            text = album.title,
                            Modifier
                                .padding(24.dp)
                                .align(Alignment.BottomStart),
                        )
                    }


                    Divider(color = Color.White, thickness = 8.dp)
                }
            }
        }
    }

@Composable
private fun albumView(album: Album): @Composable (ColumnScope.() -> Unit) =
    {
        Text(
            text = album.title,
            Modifier.padding(24.dp),
            fontSize = 18.sp
        )
        LazyColumn {
            items(album.photos) { photo ->
                AsyncImage(
                    model = photo.thumbnailSrc,
                    contentDescription = "Photo",
                    modifier = Modifier.fillMaxSize(),
                )

                Divider(color = Color.White, thickness = 48.dp)
            }
        }
    }

@Composable
fun TextWithShadow(
    text: String,
    modifier: Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 24.sp,
        style = MaterialTheme.typography.h4.copy(
            shadow = Shadow(
                color = Color.White,
                offset = Offset(2f, 2f),
                blurRadius = 24f,
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BrowserTheme {
        Text(text = "Android")
    }
}

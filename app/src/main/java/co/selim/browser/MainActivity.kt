package co.selim.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import co.selim.browser.model.Photo
import co.selim.browser.ui.theme.BrowserTheme
import co.selim.browser.ui.utils.displayText
import co.selim.browser.ui.utils.formatValue
import co.selim.browser.ui.utils.src
import co.selim.browser.ui.utils.thumbnailSrc
import co.selim.browser.viewmodel.AlbumViewModel
import co.selim.browser.viewmodel.IndexViewModel
import co.selim.browser.viewmodel.PhotoViewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {

    private val imageLoader: ImageLoader by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrowserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel = viewModel<IndexViewModel>()
                    val albums = viewModel.albums.collectAsLazyPagingItems()

                    val navController = rememberNavController()

                    Column {

                        NavHost(navController = navController, startDestination = "albums") {
                            composable("albums") {
                                Column(content = indexView(albums, navController))
                            }

                            composable(
                                "albums/{slug}",
                                arguments = listOf(navArgument("slug") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val slug = backStackEntry.arguments?.getString("slug")!!
                                val viewModel = viewModel(
                                    key = slug,
                                    initializer = {
                                        AlbumViewModel(slug)
                                    }
                                )

                                val albumState = viewModel.albumFlow.collectAsState()
                                when (val album = albumState.value) {
                                    NetworkResource.Error -> Text(text = stringResource(id = R.string.loading_failed_album))
                                    is NetworkResource.Loaded -> Column(
                                        content = albumView(
                                            album.value,
                                            navController
                                        )
                                    )
                                    NetworkResource.Loading -> Text(text = stringResource(id = R.string.loading))
                                }
                            }

                            composable(
                                "photos/{uri}",
                                arguments = listOf(navArgument("uri") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val uri = backStackEntry.arguments?.getString("uri")!!
                                val viewModel = viewModel(
                                    key = uri,
                                    initializer = {
                                        PhotoViewModel(uri)
                                    }
                                )

                                val photoState = viewModel.photoFlow.collectAsState()

                                when (val photo = photoState.value) {
                                    NetworkResource.Error -> Text(text = stringResource(id = R.string.loading_failed_photo))
                                    is NetworkResource.Loaded -> Column(content = photoView(photo.value))
                                    NetworkResource.Loading -> Text(text = stringResource(id = R.string.loading))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun indexView(
        albums: LazyPagingItems<Album>,
        navController: NavHostController,
    ): @Composable (ColumnScope.() -> Unit) =
        {
            Text(
                text = stringResource(id = R.string.title_app),
                Modifier.padding(24.dp),
                fontSize = 24.sp
            )

            Text(
                text = stringResource(id = R.string.title_albums),
                Modifier.padding(24.dp),
                fontSize = 18.sp
            )

            LazyColumn {
                items(items = albums, key = { it.id }) { album ->
                    if (album != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { navController.navigate("albums/${album.slug}") }) {

                            AsyncImage(
                                model = album.coverPhoto.thumbnailSrc,
                                contentDescription = "${album.title} cover photo",
                                modifier = Modifier.fillMaxSize(),
                                imageLoader = imageLoader
                            )
                            TextWithShadow(
                                text = album.title,
                                Modifier
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                                    .align(Alignment.BottomStart),
                            )
                        }

                        Divider(color = Color.White, thickness = 8.dp)
                    }
                }
            }
        }

    @Composable
    private fun albumView(
        album: Album,
        navController: NavHostController,
        modifier: Modifier = Modifier,
    ): @Composable (ColumnScope.() -> Unit) =
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
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { navController.navigate("photos/${photo.uri}") },
                        contentScale = ContentScale.FillWidth,
                        imageLoader = imageLoader,
                    )

                    Divider(color = Color.White, thickness = 48.dp)
                }
            }
        }

    @Composable
    private fun photoView(
        photo: Photo,
        modifier: Modifier = Modifier,
    ): @Composable (ColumnScope.() -> Unit) =
        {
            Text(
                text = stringResource(id = R.string.title_photo),
                Modifier.padding(24.dp),
                fontSize = 18.sp
            )
            Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
                AsyncImage(
                    model = photo.src,
                    contentDescription = "Photo",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillWidth,
                    imageLoader = imageLoader
                )

                photo.exifData.forEach { (exifKey, value) ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        Text(
                            text = exifKey.displayText.resolve(LocalContext.current),
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        Text(
                            text = exifKey.formatValue(value),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }

                Divider(color = Color.White, thickness = 48.dp)
            }
        }
}

@Composable
fun TextWithShadow(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 24.sp,
        style = MaterialTheme.typography.h4.copy(
            shadow = Shadow(
                color = Color.LightGray,
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

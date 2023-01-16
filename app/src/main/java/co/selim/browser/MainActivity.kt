package co.selim.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
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
                    val albums = viewModel.getAlbums().collectAsLazyPagingItems()

                    val navController = rememberNavController()

                    Column {
                        Text(text = "selim.co", Modifier.padding(24.dp), fontSize = 24.sp)

                        NavHost(navController = navController, startDestination = "albums") {
                            composable("albums") {
                                Text(text = "Albums", Modifier.padding(24.dp), fontSize = 16.sp)

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

                            composable(
                                "album/{slug}",
                                arguments = listOf(navArgument("slug") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                Text(text = backStackEntry.arguments?.getString("slug").orEmpty())
                            }
                        }
                    }
                }
            }
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

package com.example.runpath.ui.theme

import FeedReaderDbHelper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.example.runpath.R

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    width: Int,
    height: Int
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier.fillMaxWidth().fillMaxHeight(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ImageSlider(images: List<Any>) {
    var currentImageIndex by remember { mutableStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f).height(100.dp)
            .fillMaxWidth().padding(16.dp)) {
            // Scrollable Row of Cards
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(images) { index, image ->
                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .height(200.dp)
                            .clickable {
                                if (index != currentImageIndex && !isAnimating) {
                                    isAnimating = true
                                    coroutineScope.launch {
                                        val delayMillis = 500L
                                        // Wait for the card to change color before animating
                                        delay(delayMillis / 2)
                                        currentImageIndex = index
                                        delay(delayMillis)
                                        isAnimating = false
                                    }
                                }
                            },
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                        ) {
                        NetworkImage(
                            contentDescription = "",
                            url = image as String,
                            width = 300,
                            height = 200
                        )
                    }
                }

            }

        }
    }
    LaunchedEffect(currentImageIndex) {
        while (true) {
            delay(5000L)
            if (!isAnimating) {
                val nextIndex = (currentImageIndex + 1) % images.size
                currentImageIndex = nextIndex
            }
        }
    }
}


@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun HomePage( dbHelper: FeedReaderDbHelper) {
    Column() {

        val images = listOf(
            "https://media.self.com/photos/64063998753c98598ce42cde/1:1/w_5588,h_5588,c_limit/benfits%20of%20running.jpeg",
            "https://i0.wp.com/post.healthline.com/wp-content/uploads/2021/06/running-runner-1296x728-header.jpg?w=1155&h=1528",
            "https://static.nike.com/a/images/f_auto/dpr_3.0,cs_srgb/w_403,c_limit/37967987-f313-41f8-b8c5-74928012d043/5-coach-approved-tips-to-get-better-at-running-yes-really.jpg"
        )

        ImageSlider(images)
    }

}
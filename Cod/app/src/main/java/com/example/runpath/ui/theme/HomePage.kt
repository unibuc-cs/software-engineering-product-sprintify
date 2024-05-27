import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ImageSlider(images: List<String>) {
    var currentImageIndex by remember { mutableStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) {
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
                                        delay(delayMillis / 2)
                                        currentImageIndex = index
                                        delay(delayMillis)
                                        isAnimating = false
                                    }
                                }
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        NetworkImage(
                            contentDescription = "Running image",
                            url = image,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }

    // Automatic slideshow
    LaunchedEffect(currentImageIndex) {
        while (true) {
            delay(5000L) // Slide every 5 seconds
            if (!isAnimating) {
                val nextIndex = (currentImageIndex + 1) % images.size
                currentImageIndex = nextIndex
            }
        }
    }
}

@Composable
fun SprintifyTitleCard() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Sprintify",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your Running Companion, Every Step of the Way",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun HomePage(navController: NavController) {

    val images = listOf(
        "https://media.self.com/photos/64063998753c98598ce42cde/1:1/w_5588,h_5588,c_limit/benfits%20of%20running.jpeg",
        "https://i0.wp.com/post.healthline.com/wp-content/uploads/2021/06/running-runner-1296x728-header.jpg?w=1155&h=1528",
        "https://static.nike.com/a/images/f_auto/dpr_3.0,cs_srgb/w_403,c_limit/37967987-f313-41f8-b8c5-74928012d043/5-coach-approved-tips-to-get-better-at-running-yes-really.jpg"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2)) // Purple background for the entire screen
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageSlider(images)
        SprintifyTitleCard()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate("loginPage") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log in", color = Color.Black)
            }
            Button(
                onClick = { navController.navigate("registerPage") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign up", color = Color.Black)
            }
        }
    }
}

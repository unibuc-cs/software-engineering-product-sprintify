import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.runpath.R
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RunPage(onOptionSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Start Your Run",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        val choices = listOf("From a Circuit", "From a Previous Run", "Freemode")
        val images = listOf(
            painterResource(id = R.drawable.circuit),
            painterResource(id = R.drawable.previous_run),
            painterResource(id = R.drawable.freemode)
        )

        choices.forEachIndexed { index, choice ->
            RunChoiceItem(choice = choice, image = images[index], onClick = { onOptionSelected(choice) })
        }
    }
}

@Composable
fun RunChoiceItem(choice: String, image: Painter, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFe6d9fdL))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = choice, fontSize = 20.sp)
        }
    }
}


@Composable
fun CircuitScreen() {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Circuit Screen", fontSize = 24.sp)
    }
}

@Composable
fun PreviousRunScreen() {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Previous Run Screen", fontSize = 24.sp)
    }
}

@Composable
fun FreemodeScreen() {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Freemode Screen", fontSize = 24.sp)
    }
}


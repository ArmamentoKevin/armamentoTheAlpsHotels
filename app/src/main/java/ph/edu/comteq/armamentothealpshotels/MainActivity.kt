package ph.edu.comteq.armamentothealpshotels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import ph.edu.comteq.armamentothealpshotels.ui.theme.ArmamentoTheAlpsHotelsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ArmamentoTheAlpsHotelsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Homepage(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
// Display the Homepage
fun Homepage(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hotels by remember { mutableStateOf(emptyList<Hotel>()) }

    // load Json data
    LaunchedEffect(Unit) {
        val json = context.assets.open("hotels.json")
            .bufferedReader()
            .use { it.readText() }
        val gson = Gson()
        val hotelsArray = gson.fromJson(json,Array<Hotel>::class.java)
        hotels = hotelsArray.toList()
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Header of the Homepage
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Name of the Hotel - The Alps Hotels
            Text(
                text = "The Alps Hotels",
                fontSize = 24.sp,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.width(8.dp))

            // France flag Image
            Image(
                painter = painterResource(
                    id = R.drawable.france_national_flag
                ),
                contentDescription = "France Flag",
                modifier = Modifier.size(32.dp)
            )

            // Move the Person Image to the right
            Spacer(modifier = Modifier.weight(1f))

            // Person image
            Image(
                painter = painterResource(
                    id = R.drawable.person
                ),
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Search hotels...")
            },
            shape = RoundedCornerShape(12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(10.dp)
        ) {
            items(hotels) { hotel ->
                Text(hotel.hotel_name)
            }
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun GreetingPreview() {
    ArmamentoTheAlpsHotelsTheme {
        Homepage("Android")
    }
}

package ph.edu.comteq.armamentothealpshotels

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import ph.edu.comteq.armamentothealpshotels.ui.theme.ArmamentoTheAlpsHotelsTheme

private val PrimaryBlue = Color(0xFF1E3A8A)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val BackgroundColor = Color(0xFFF8FAFC)
private val StarGold = Color(0xFFD97706)
private val GoldBackground = Color(0xFFFEF3C7)
private val ChipBackground = Color(0xFFF1F5F9)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ArmamentoTheAlpsHotelsTheme {
                var selectedHotel by remember { mutableStateOf<Hotel?>(null) }

                val currentHotel = selectedHotel
                if (currentHotel == null) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = BackgroundColor
                    ) { innerPadding ->

                        Homepage(
                            modifier = Modifier.padding(innerPadding),
                            onHotelClick = { hotel ->
                                selectedHotel = hotel
                            }
                        )
                    }
                } else {
                    BookingDetailsScreen(
                        hotelId = currentHotel.hotel_id,
                        hotelName = currentHotel.hotel_name,
                        onBackClick = {
                            selectedHotel = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Homepage(
    modifier: Modifier = Modifier,
    onHotelClick: (Hotel) -> Unit = {}
) {

    val context = LocalContext.current
    val isInspectionMode = LocalInspectionMode.current

    // Stores All Hotels
    var hotels by remember {
        mutableStateOf(
            if (isInspectionMode) {
                listOf(
                    Hotel(
                        hotel_id = 1000,
                        hotel_name = "Résidence Pierre & Vacances Premium les Crets",
                        hotel_rating = 8.3,
                        hotel_to_ski_distance = 6.7,
                        hotel_cover_image = "cover/1000.jpg"
                    ),
                    Hotel(
                        hotel_id = 1001,
                        hotel_name = "Duplex - Plein sud - Pied des pistes - 50m2 - Mottaret",
                        hotel_rating = 9.4,
                        hotel_to_ski_distance = 6.4,
                        hotel_cover_image = "cover/1001.jpg"
                    ),
                    Hotel(
                        hotel_id = 1002,
                        hotel_name = "Meribel le chalet d'eugénie",
                        hotel_rating = 7.8,
                        hotel_to_ski_distance = 0.8,
                        hotel_cover_image = "cover/1002.jpg"
                    ),
                    Hotel(
                        hotel_id = 1003,
                        hotel_name = "Chalet de 3 chambres a Les Allues",
                        hotel_rating = 7.1,
                        hotel_to_ski_distance = 3.2,
                        hotel_cover_image = "cover/1003.jpg"
                    ),
                    Hotel(
                        hotel_id = 1004,
                        hotel_name = "Résidence Premium L'Hévana",
                        hotel_rating = 7.9,
                        hotel_to_ski_distance = 3.8,
                        hotel_cover_image = "cover/1004.jpg"
                    ),
                    Hotel(
                        hotel_id = 1005,
                        hotel_name = "L'Hévana - maeva Home - Appartement",
                        hotel_rating = 8.2,
                        hotel_to_ski_distance = 3.8,
                        hotel_cover_image = "cover/1005.jpg"
                    ),
                    Hotel(
                        hotel_id = 1006,
                        hotel_name = "La Belette - Duplex au charme savoyard avec vue sur la montagne",
                        hotel_rating = 6.7,
                        hotel_to_ski_distance = 1.8,
                        hotel_cover_image = "cover/1006.jpg"
                    ),
                    Hotel(
                        hotel_id = 1007,
                        hotel_name = "Superbe Appartement au Plateau Rond",
                        hotel_rating = 6.9,
                        hotel_to_ski_distance = 3.7,
                        hotel_cover_image = "cover/1007.jpg"
                    ),
                    Hotel(
                        hotel_id = 1008,
                        hotel_name = "Appartement Méribel",
                        hotel_rating = 9.0,
                        hotel_to_ski_distance = 6.5,
                        hotel_cover_image = "cover/1008.jpg"
                    ),
                    Hotel(
                        hotel_id = 1009,
                        hotel_name = "L'Appart à Méribel",
                        hotel_rating = 7.0,
                        hotel_to_ski_distance = 6.2,
                        hotel_cover_image = "cover/1009.jpg"
                    )
                )
            } else {
                emptyList()
            }
        )
    }

    // Stores Search Text
    var searchText by remember {
        mutableStateOf("")
    }

    // Loads Hotel Data
    LaunchedEffect(Unit) {
        try {
            val json = context.assets
                .open("cover/hotels.json")
                .bufferedReader()
                .use { it.readText() }

            val gson = Gson()

            val hotelsArray = gson.fromJson(
                json,
                Array<Hotel>::class.java
            )

            hotels = hotelsArray.toList()
        } catch (_: Exception) {
            // Fallback handled by default list
        }
    }

    // Filters Hotel Results
    val filteredHotels = hotels.filter { hotel ->

        hotel.hotel_name.contains(
            searchText,
            ignoreCase = true
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Hotel Title
            Text(
                text = "The Alps Hotels",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            // France Flag
            Image(
                painter = painterResource(
                    id = R.drawable.france_national_flag
                ),
                contentDescription = "France Flag",
                modifier = Modifier.size(32.dp)
            )

            // Pushes Profile To Right
            Spacer(
                modifier = Modifier.weight(1f)
            )

            // Profile Icon
            Image(
                painter = painterResource(
                    id = R.drawable.person
                ),
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search hotels...",
                    color = TextMuted
                )
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Hotel List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            items(filteredHotels) { hotel ->

                HotelItem(
                    hotel = hotel,
                    onClick = { onHotelClick(hotel) }
                )
            }
        }
    }
}

@Composable
fun HotelItem(
    hotel: Hotel,
    onClick: () -> Unit = {}
) {

    val context = LocalContext.current

    // Loads Hotel Image
    val imageBitmap = remember(
        hotel.hotel_cover_image
    ) {

        try {

            BitmapFactory
                .decodeStream(
                    context.assets.open(
                        hotel.hotel_cover_image
                    )
                )

        } catch (_: Exception) {

            try {
                BitmapFactory
                    .decodeStream(
                        context.assets.open("cover/1001.jpg")
                    )
            } catch (_: Exception) {
                null
            }
        }
    }

    // Hotel Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Hotel Image
            if (imageBitmap != null) {

                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = hotel.hotel_name,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(
                            RoundedCornerShape(12.dp)
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            // Hotel Information
            Column(
                modifier = Modifier.weight(1f)
            ) {

                // Hotel Name
                Text(
                    text = hotel.hotel_name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Rating Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldBackground
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "★ ",
                                color = StarGold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${hotel.hotel_rating}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = StarGold
                            )
                        }
                    }

                    // Ski Distance Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ChipBackground
                    ) {
                        Text(
                            text = "⛷ ${hotel.hotel_to_ski_distance} km",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// Preview
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun GreetingPreview() {

    ArmamentoTheAlpsHotelsTheme {
        Homepage()
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun BookingDetailsPreview() {

    ArmamentoTheAlpsHotelsTheme {
        BookingDetailsScreen(
            hotelId = 1000,
            hotelName = "Résidence Pierre & Vacances Premium les Crets",
            onBackClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun RoomSelectionPreview() {

    ArmamentoTheAlpsHotelsTheme {
        BookingDetailsScreen(
            hotelId = 1000,
            hotelName = "Résidence Pierre & Vacances Premium les Crets",
            onBackClick = {},
            initialTabIndex = 1
        )
    }
}

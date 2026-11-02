package ph.edu.comteq.armamentothealpshotels

import android.graphics.BitmapFactory
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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

@Composable
fun Homepage(
    modifier: Modifier = Modifier,
    onHotelClick: (Hotel) -> Unit = {},
    onMyBookingsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isInspectionMode = LocalInspectionMode.current

    // Stores All Hotels
    var hotels by remember {
        mutableStateOf(
            if (isInspectionMode) {
                getSampleHotels()
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
            hotels = getSampleHotels()
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

            Spacer(modifier = Modifier.width(8.dp))

            // France Flag
            Image(
                painter = painterResource(id = R.drawable.france_national_flag),
                contentDescription = "France Flag",
                modifier = Modifier.size(32.dp)
            )

            // Pushes Profile / My Bookings To Right
            Spacer(modifier = Modifier.weight(1f))

            // My Bookings Profile Icon
            IconButton(onClick = onMyBookingsClick) {
                Image(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = "My Bookings",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
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

        Spacer(modifier = Modifier.height(16.dp))

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
    val imageBitmap = remember(hotel.hotel_cover_image) {
        try {
            BitmapFactory.decodeStream(context.assets.open(hotel.hotel_cover_image))
        } catch (_: Exception) {
            try {
                BitmapFactory.decodeStream(context.assets.open("cover/1001.jpg"))
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
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

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

fun getSampleHotels(): List<Hotel> {
    return listOf(
        Hotel(1000, "Résidence Pierre & Vacances Premium les Crets", 8.3, 6.7, "cover/1000.jpg"),
        Hotel(1001, "Duplex - Plein sud - Pied des pistes - 50m2 - Mottaret", 9.4, 6.4, "cover/1001.jpg"),
        Hotel(1002, "Meribel le chalet d'eugénie", 7.8, 0.8, "cover/1002.jpg"),
        Hotel(1003, "Chalet de 3 chambres a Les Allues", 7.1, 3.2, "cover/1003.jpg"),
        Hotel(1004, "Résidence Premium L'Hévana", 7.9, 3.8, "cover/1004.jpg"),
        Hotel(1005, "L'Hévana - maeva Home - Appartement", 8.2, 3.8, "cover/1005.jpg"),
        Hotel(1006, "La Belette - Duplex au charme savoyard avec vue sur la montagne", 6.7, 1.8, "cover/1006.jpg"),
        Hotel(1007, "Superbe Appartement au Plateau Rond", 6.9, 3.7, "cover/1007.jpg"),
        Hotel(1008, "Appartement Méribel", 9.0, 6.5, "cover/1008.jpg"),
        Hotel(1009, "L'Appart à Méribel", 7.0, 6.2, "cover/1009.jpg")
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomepagePreview() {
    ArmamentoTheAlpsHotelsTheme {
        Homepage()
    }
}

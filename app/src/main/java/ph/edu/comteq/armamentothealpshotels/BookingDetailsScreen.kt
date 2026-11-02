package ph.edu.comteq.armamentothealpshotels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import java.util.Locale

// Theme Palette
private val PrimaryBlue = Color(0xFF1E3A8A)
private val PrimaryBlueTint = Color(0xFFEFF6FF)
private val AccentRed = Color(0xFFDC2626)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val BorderColor = Color(0xFFCBD5E1)
private val ChipBackground = Color(0xFFF1F5F9)
private val BackgroundColor = Color(0xFFF8FAFC)

@Composable
fun BookingDetailsScreen(
    hotelId: Int,
    hotelName: String,
    onBackClick: () -> Unit,
    onRoomClick: (Room) -> Unit = {},
    initialTabIndex: Int = 0
) {
    val context = LocalContext.current
    val isInspectionMode = LocalInspectionMode.current
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    var hotelDetails by remember { mutableStateOf<HotelDetails?>(null) }

    LaunchedEffect(hotelId) {
        val targetId = if (hotelId == 1008) 1008 else 1000
        try {
            val json = context.assets
                .open("cover/hotels_details.$targetId.json")
                .bufferedReader()
                .use { it.readText() }

            val gson = Gson()
            hotelDetails = gson.fromJson(json, HotelDetails::class.java)
        } catch (_: Exception) {
            hotelDetails = getFallbackHotelDetails(targetId)
        }
    }

    if (isInspectionMode && hotelDetails == null) {
        hotelDetails = getFallbackHotelDetails(if (hotelId == 1008) 1008 else 1000)
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        BackArrowIcon(
                            color = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Booking",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                // Subpage Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = AccentRed,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = AccentRed
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Text(
                                text = "Guest reviews",
                                fontSize = 16.sp,
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == 0) AccentRed else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Text(
                                text = "Room selection",
                                fontSize = 16.sp,
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == 1) AccentRed else TextMuted
                            )
                        }
                    )
                }
            }
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Hotel Information Header
            Text(
                text = hotelDetails?.hotel_name ?: hotelName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            val details = hotelDetails
            if (details != null) {
                if (selectedTabIndex == 0) {
                    GuestReviewsContent(guestReviews = details.guest_reviews)
                } else {
                    RoomSelectionContent(rooms = details.rooms, onRoomClick = onRoomClick)
                }
            }
        }
    }
}

@Composable
fun GuestReviewsContent(guestReviews: GuestReviews) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ratings Area
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Ratings",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    guestReviews.ratings_categories.forEach { categoryMap ->
                        val categoryName = categoryMap.keys.firstOrNull() ?: ""
                        val ratingValue = categoryMap.values.firstOrNull() ?: 0.0

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = categoryName,
                                    fontSize = 15.sp,
                                    color = TextDark,
                                    fontWeight = FontWeight.Medium
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PrimaryBlueTint,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.1f", ratingValue),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (ratingValue / 10.0).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF2563EB),
                                trackColor = Color(0xFFE2E8F0),
                            )
                        }
                    }
                }
            }
        }

        // Reviews Area
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Reviews",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(guestReviews.reviews_objects) { review ->
                            ReviewItemCard(review = review)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItemCard(review: ReviewObject) {
    OutlinedCard(
        modifier = Modifier
            .width(280.dp)
            .height(260.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val initial = review.username.firstOrNull()?.toString()?.uppercase(Locale.US) ?: "A"
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = review.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = review.country,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.review_text,
                fontSize = 13.sp,
                color = TextDark,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomSelectionContent(
    rooms: List<Room>,
    onRoomClick: (Room) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Rooms",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rooms.forEach { room ->
                            RoomItemCard(
                                room = room,
                                onRoomClick = onRoomClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomItemCard(
    room: Room,
    onRoomClick: (Room) -> Unit = {}
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRoomClick(room) },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = room.room_type,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bed: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextDark
                )
                Text(
                    text = room.room_bed_type,
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Total number of guests: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextDark
                )
                Text(
                    text = "${room.room_total_number_of_guests}",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Room features chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .border(BorderStroke(1.dp, BorderColor), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = room.room_features.joinToString(", "),
                    fontSize = 12.sp,
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price text
            Text(
                text = "€ ${room.room_price_for_one_night}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BackArrowIcon(modifier: Modifier = Modifier, color: Color = PrimaryBlue) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.65f, h * 0.2f)
            lineTo(w * 0.35f, h * 0.5f)
            lineTo(w * 0.65f, h * 0.8f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

private fun getFallbackHotelDetails(targetId: Int): HotelDetails {
    return if (targetId == 1008) {
        HotelDetails(
            hotel_id = 1008,
            hotel_name = "Appartement Méribel",
            guest_reviews = GuestReviews(
                ratings_categories = listOf(
                    mapOf("Staff" to 7.7),
                    mapOf("Facilities" to 9.5),
                    mapOf("Value for money" to 7.5)
                ),
                reviews_objects = listOf(
                    ReviewObject("Simon", "United Kingdom", "Good size apartment, well equiped kitchen, except oven."),
                    ReviewObject("Tomáš", "Slovakia", "Direct access to slopes from the apartment doors was excellent."),
                    ReviewObject("Kerem", "Turkey", "Great location, renovated rooms and friendly staff.")
                )
            ),
            rooms = listOf(
                Room(1008000, "Single room with river view", "1 large bed", 1, listOf("Heating", "Dining table", "15 m²"), 90),
                Room(1008001, "Double Room", "1 large double bed", 2, listOf("Soundproofing", "16 m²", "Refrigerator"), 144)
            )
        )
    } else {
        HotelDetails(
            hotel_id = 1000,
            hotel_name = "Résidence Pierre & Vacances Premium les Crets",
            guest_reviews = GuestReviews(
                ratings_categories = listOf(
                    mapOf("Staff" to 8.7),
                    mapOf("Facilities" to 8.4),
                    mapOf("Cleanliness" to 8.6)
                ),
                reviews_objects = listOf(
                    ReviewObject("Petri", "Finland", "Location was excellent. In D building we could ski in/out."),
                    ReviewObject("Lionel", "Australia", "the apartment was spacious, clean and really well set up."),
                    ReviewObject("Barroso", "Portugal", "Well located, with supermarket and ski rental just under.")
                )
            ),
            rooms = listOf(
                Room(1000000, "One-Bedroom Apartment - Open Mountain View", "2 double beds", 4, listOf("1 sofa bed", "35 m²", "Mountain view"), 142),
                Room(1000001, "One-Bedroom Apartment", "2 double beds", 4, listOf("30 m²", "Dishwasher", "Flat-screen TV"), 105)
            )
        )
    }
}

package ph.edu.comteq.armamentothealpshotels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.comteq.armamentothealpshotels.ui.theme.ArmamentoTheAlpsHotelsTheme

private val PrimaryBlue = Color(0xFF1E3A8A)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val BorderColor = Color(0xFFCBD5E1)
private val BackgroundColor = Color(0xFFF8FAFC)

@Composable
fun MyBookingsScreen(
    onBackClick: () -> Unit = {},
    viewModel: BookingViewModel? = null
) {
    val isInspectionMode = LocalInspectionMode.current
    val bookingsState by if (viewModel != null) {
        viewModel.allBookings.collectAsState(initial = emptyList())
    } else {
        remember {
            mutableStateOf(
                if (isInspectionMode) getSampleBookings() else emptyList()
            )
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        text = "My Bookings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                Text(
                    text = "List of my bookings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                )
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
            if (bookingsState.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No bookings yet.\nBook your hotel stay now!",
                        fontSize = 18.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(bookingsState) { index, booking ->
                        BookingItemCard(
                            position = index + 1,
                            booking = booking
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItemCard(
    position: Int,
    booking: Booking
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Number / Position
            Text(
                text = "$position",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.width(36.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Name
                Text(
                    text = "${booking.firstName} ${booking.lastName}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Hotel Name
                Text(
                    text = booking.hotelName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Dates
                Text(
                    text = "${booking.checkInDate}  to  ${booking.checkOutDate}",
                    fontSize = 14.sp,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Guests and Rooms
                val roomText = if (booking.rooms > 1) "Rooms" else "Room"
                Text(
                    text = "${booking.adults} Adults, ${booking.children} Children, ${booking.rooms} $roomText",
                    fontSize = 14.sp,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Travel type and Payment method
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = booking.travelType,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        val paymentFormatted = when (booking.paymentMethod.lowercase()) {
                            "cash" -> "Pay with cash"
                            "credit card" -> "Pay with credit card"
                            "e-pay" -> "Pay with E-Pay"
                            else -> "Pay with ${booking.paymentMethod}"
                        }
                        Text(
                            text = paymentFormatted,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }

                    // Total Price
                    Text(
                        text = "€ ${booking.totalPrice}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

private fun getSampleBookings(): List<Booking> {
    return listOf(
        Booking(
            id = 1,
            firstName = "Petri",
            lastName = "Mottaret",
            hotelName = "Résidence Pierre & Vacances Premium les Crets",
            roomType = "Double Room with Matterhorn View",
            checkInDate = "Tue, Sep 10, 2024",
            checkOutDate = "Sun, Sep 15, 2024",
            adults = 2,
            children = 0,
            rooms = 1,
            travelType = "For sightseeing",
            paymentMethod = "Cash",
            totalPrice = 1495
        ),
        Booking(
            id = 2,
            firstName = "Simon",
            lastName = "Walker",
            hotelName = "Appartement Méribel",
            roomType = "Single room with river view",
            checkInDate = "Wed, Oct 12, 2024",
            checkOutDate = "Mon, Oct 17, 2024",
            adults = 1,
            children = 0,
            rooms = 1,
            travelType = "For business",
            paymentMethod = "Credit card",
            totalPrice = 600
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyBookingsPreview() {
    ArmamentoTheAlpsHotelsTheme {
        MyBookingsScreen()
    }
}

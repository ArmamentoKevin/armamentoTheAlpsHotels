package ph.edu.comteq.armamentothealpshotels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.comteq.armamentothealpshotels.ui.theme.ArmamentoTheAlpsHotelsTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil

private val PrimaryBlue = Color(0xFF1E3A8A)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val BorderColor = Color(0xFFCBD5E1)
private val BackgroundColor = Color(0xFFF8FAFC)
private val AccentRed = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmScreen(
    hotelName: String,
    roomType: String,
    bedInfo: String,
    maxGuests: Int,
    pricePerNight: Int,
    onBackClick: () -> Unit = {},
    onBookingSuccess: () -> Unit = {},
    viewModel: BookingViewModel? = null
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.US) }

    // Initial Date setup: Check-in today, Check-out in 5 days
    val initialCheckInMillis = remember {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }
    val initialCheckOutMillis = remember { initialCheckInMillis + (5L * 24 * 60 * 60 * 1000) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var firstNameTouched by remember { mutableStateOf(false) }
    var lastNameTouched by remember { mutableStateOf(false) }
    var attemptedSubmit by remember { mutableStateOf(false) }

    var checkInMillis by remember { mutableLongStateOf(initialCheckInMillis) }
    var checkOutMillis by remember { mutableLongStateOf(initialCheckOutMillis) }

    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }

    var adultsText by remember { mutableStateOf("2") }
    var childrenText by remember { mutableStateOf("0") }
    var adultsTouched by remember { mutableStateOf(false) }

    val adults = adultsText.toIntOrNull() ?: 0
    val children = childrenText.toIntOrNull() ?: 0

    // Auto-calculated rooms = ceil((adults + children) / maxGuests), minimum 1
    val calculatedRooms by remember(adults, children, maxGuests) {
        derivedStateOf {
            val capacity = if (maxGuests > 0) maxGuests else 1
            val totalPeople = adults + children
            val count = ceil(totalPeople.toDouble() / capacity).toInt()
            if (count < 1) 1 else count
        }
    }

    // Travel for business? radio group: "For sightseeing" (default, +€0) vs "For business with a meeting room" (+€150)
    var isBusinessTravel by remember { mutableStateOf(false) }

    // Which way to pay? radio group: "Cash", "Credit card", "E-Pay"
    var paymentMethod by remember { mutableStateOf("Cash") }

    // Calculate number of nights
    val numberOfNights by remember(checkInMillis, checkOutMillis) {
        derivedStateOf {
            val diff = checkOutMillis - checkInMillis
            val days = (diff / (24 * 60 * 60 * 1000)).toInt()
            if (days < 1) 1 else days
        }
    }

    // Total price formula = (pricePerNight * numberOfNights * rooms) + (isBusiness ? 150 : 0)
    val totalPrice by remember(pricePerNight, numberOfNights, calculatedRooms, isBusinessTravel) {
        derivedStateOf {
            val roomTotal = pricePerNight * numberOfNights * calculatedRooms
            val extra = if (isBusinessTravel) 150 else 0
            roomTotal + extra
        }
    }

    // Form Validation Rules
    val isFirstNameValid = firstName.isNotBlank()
    val isLastNameValid = lastName.isNotBlank()
    val isDatesValid = checkOutMillis > checkInMillis
    val isGuestsValid = adults >= 1 && children >= 0
    val isFormValid = isFirstNameValid && isLastNameValid && isDatesValid && isGuestsValid

    val showFirstNameError = (firstNameTouched || attemptedSubmit) && !isFirstNameValid
    val showLastNameError = (lastNameTouched || attemptedSubmit) && !isLastNameValid
    val showAdultsError = (adultsTouched || attemptedSubmit) && adults < 1

    // SelectableDates to prevent check-out on or before check-in date
    val checkOutSelectableDates = remember(checkInMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis > checkInMillis
            }
        }
    }

    // DatePickers
    if (showCheckInPicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = checkInMillis)
        DatePickerDialog(
            onDismissRequest = { showCheckInPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        checkInMillis = millis
                        if (checkOutMillis <= checkInMillis) {
                            checkOutMillis = checkInMillis + (24 * 60 * 60 * 1000)
                        }
                    }
                    showCheckInPicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckInPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showCheckOutPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = checkOutMillis,
            selectableDates = checkOutSelectableDates
        )
        DatePickerDialog(
            onDismissRequest = { showCheckOutPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        checkOutMillis = millis
                    }
                    showCheckOutPicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckOutPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
                        text = "Booking Confirm",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                Text(
                    text = "You are going to reserve:",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hotel Name Header
            Text(
                text = hotelName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Room Summary Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
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
                        text = roomType,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Text(text = "Bed: ", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                            Text(text = bedInfo, fontSize = 13.sp, color = TextMuted)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Total number of guests: ", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                            Text(text = "$maxGuests", fontSize = 13.sp, color = TextMuted)
                        }
                        Text(
                            text = "€ $pricePerNight",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            // Form Section Title
            Text(
                text = "Form",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            // Form Content OutlinedCard
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // First Name and Last Name Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = {
                                    firstName = it
                                    firstNameTouched = true
                                },
                                label = { Text("First Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = showFirstNameError,
                                supportingText = {
                                    if (showFirstNameError) {
                                        Text("First name is required", color = AccentRed)
                                    }
                                }
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = {
                                    lastName = it
                                    lastNameTouched = true
                                },
                                label = { Text("Last Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = showLastNameError,
                                supportingText = {
                                    if (showLastNameError) {
                                        Text("Last name is required", color = AccentRed)
                                    }
                                }
                            )
                        }
                    }

                    // Dates Row (Check-in & Check-out)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showCheckInPicker = true }
                        ) {
                            OutlinedTextField(
                                value = dateFormat.format(Date(checkInMillis)),
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("Check-in date") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showCheckOutPicker = true }
                        ) {
                            OutlinedTextField(
                                value = dateFormat.format(Date(checkOutMillis)),
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("Check-out date") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = !isDatesValid,
                                supportingText = {
                                    if (!isDatesValid) {
                                        Text("Check-out date must be after check-in date", color = AccentRed)
                                    }
                                }
                            )
                        }
                    }

                    // Room Type Label
                    Text(
                        text = roomType,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    // Adults, Children, and Room Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        OutlinedTextField(
                            value = adultsText,
                            onValueChange = {
                                adultsText = it.filter { char -> char.isDigit() }
                                adultsTouched = true
                            },
                            label = { Text("Adults") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = showAdultsError,
                            supportingText = {
                                if (showAdultsError) {
                                    Text("At least 1 adult is required", color = AccentRed)
                                }
                            }
                        )
                        OutlinedTextField(
                            value = childrenText,
                            onValueChange = { childrenText = it.filter { char -> char.isDigit() } },
                            label = { Text("Children") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Column(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
                            Text(text = "Room", fontSize = 12.sp, color = TextMuted)
                            Text(
                                text = "$calculatedRooms",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Travel for business? Radio Group
                    Column {
                        Text(
                            text = "Travel for business?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextDark
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isBusinessTravel = false }
                        ) {
                            RadioButton(
                                selected = !isBusinessTravel,
                                onClick = { isBusinessTravel = false },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                            )
                            Text(text = "For sightseeing", fontSize = 14.sp, color = TextDark)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isBusinessTravel = true }
                        ) {
                            RadioButton(
                                selected = isBusinessTravel,
                                onClick = { isBusinessTravel = true },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                            )
                            Text(
                                text = "+ € 150\nFor business with a meeting room",
                                fontSize = 14.sp,
                                color = TextDark
                            )
                        }
                    }

                    // Which way to pay? Radio Group & Total Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "Which way to pay?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextDark
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = paymentMethod == "Cash",
                                    onClick = { paymentMethod = "Cash" },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                                )
                                Text(text = "Cash", fontSize = 13.sp, color = TextDark)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = paymentMethod == "Credit card",
                                    onClick = { paymentMethod = "Credit card" },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                                )
                                Text(text = "Credit card", fontSize = 13.sp, color = TextDark)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = paymentMethod == "E-Pay",
                                    onClick = { paymentMethod = "E-Pay" },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                                )
                                Text(text = "E-Pay", fontSize = 13.sp, color = TextDark)
                            }
                        }

                        // Total Price Display
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Text(
                                text = "€ $totalPrice",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }

                    // Book Now Button
                    Button(
                        onClick = {
                            attemptedSubmit = true
                            if (isFormValid) {
                                val booking = Booking(
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    hotelName = hotelName,
                                    roomType = roomType,
                                    checkInDate = dateFormat.format(Date(checkInMillis)),
                                    checkOutDate = dateFormat.format(Date(checkOutMillis)),
                                    adults = adults,
                                    children = children,
                                    rooms = calculatedRooms,
                                    travelType = if (isBusinessTravel) "For business" else "For sightseeing",
                                    paymentMethod = paymentMethod,
                                    totalPrice = totalPrice
                                )
                                if (viewModel != null) {
                                    viewModel.insertBooking(booking) {
                                        onBookingSuccess()
                                    }
                                } else {
                                    onBookingSuccess()
                                }
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Book now",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BookingConfirmPreview() {
    ArmamentoTheAlpsHotelsTheme {
        BookingConfirmScreen(
            hotelName = "Résidence Pierre & Vacances Premium les Crets",
            roomType = "Double Room with Matterhorn View",
            bedInfo = "Double",
            maxGuests = 2,
            pricePerNight = 1495,
            onBackClick = {},
            onBookingSuccess = {}
        )
    }
}

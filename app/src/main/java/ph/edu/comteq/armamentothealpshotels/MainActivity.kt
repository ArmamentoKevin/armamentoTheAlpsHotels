package ph.edu.comteq.armamentothealpshotels

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ph.edu.comteq.armamentothealpshotels.ui.theme.ArmamentoTheAlpsHotelsTheme

private val BackgroundColor = Color(0xFFF8FAFC)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ArmamentoTheAlpsHotelsTheme {
                val navController = rememberNavController()
                val bookingViewModel: BookingViewModel = viewModel()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BackgroundColor
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. Home Screen
                        composable("home") {
                            Homepage(
                                onHotelClick = { hotel ->
                                    val encodedName = Uri.encode(hotel.hotel_name)
                                    navController.navigate("booking_details/${hotel.hotel_id}/$encodedName")
                                },
                                onMyBookingsClick = {
                                    navController.navigate("my_bookings")
                                }
                            )
                        }

                        // 2. Booking Details Screen
                        composable(
                            route = "booking_details/{hotelId}/{hotelName}",
                            arguments = listOf(
                                navArgument("hotelId") { type = NavType.IntType },
                                navArgument("hotelName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 1000
                            val rawName = backStackEntry.arguments?.getString("hotelName") ?: ""
                            val hotelName = Uri.decode(rawName)

                            BookingDetailsScreen(
                                hotelId = hotelId,
                                hotelName = hotelName,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onRoomClick = { room ->
                                    val encHotel = Uri.encode(hotelName)
                                    val encRoomType = Uri.encode(room.room_type)
                                    val encBedInfo = Uri.encode(room.room_bed_type)
                                    val maxGuests = room.room_total_number_of_guests
                                    val price = room.room_price_for_one_night
                                    navController.navigate("booking_confirm/$encHotel/$encRoomType/$encBedInfo/$maxGuests/$price")
                                }
                            )
                        }

                        // 3. Booking Confirm Screen
                        composable(
                            route = "booking_confirm/{hotelName}/{roomType}/{bedInfo}/{maxGuests}/{pricePerNight}",
                            arguments = listOf(
                                navArgument("hotelName") { type = NavType.StringType },
                                navArgument("roomType") { type = NavType.StringType },
                                navArgument("bedInfo") { type = NavType.StringType },
                                navArgument("maxGuests") { type = NavType.IntType },
                                navArgument("pricePerNight") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val hotelName = Uri.decode(backStackEntry.arguments?.getString("hotelName") ?: "")
                            val roomType = Uri.decode(backStackEntry.arguments?.getString("roomType") ?: "")
                            val bedInfo = Uri.decode(backStackEntry.arguments?.getString("bedInfo") ?: "")
                            val maxGuests = backStackEntry.arguments?.getInt("maxGuests") ?: 2
                            val pricePerNight = backStackEntry.arguments?.getInt("pricePerNight") ?: 100

                            BookingConfirmScreen(
                                hotelName = hotelName,
                                roomType = roomType,
                                bedInfo = bedInfo,
                                maxGuests = maxGuests,
                                pricePerNight = pricePerNight,
                                viewModel = bookingViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onBookingSuccess = {
                                    navController.navigate("my_bookings") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            )
                        }

                        // 4. My Bookings Screen
                        composable("my_bookings") {
                            MyBookingsScreen(
                                viewModel = bookingViewModel,
                                onBackClick = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

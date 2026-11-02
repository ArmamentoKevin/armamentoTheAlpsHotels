package ph.edu.comteq.armamentothealpshotels

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room Entity representing a hotel booking record
@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val hotelName: String,
    val roomType: String,
    val checkInDate: String,
    val checkOutDate: String,
    val adults: Int,
    val children: Int,
    val rooms: Int,
    val travelType: String,
    val paymentMethod: String,
    val totalPrice: Int
)

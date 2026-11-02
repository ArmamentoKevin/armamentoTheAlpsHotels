package ph.edu.comteq.armamentothealpshotels

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Data Access Object for Room Database
@Dao
interface BookingDao {
    @Insert
    suspend fun insertBooking(booking: Booking)

    @Query("SELECT * FROM bookings ORDER BY id ASC")
    fun getAllBookings(): Flow<List<Booking>>
}

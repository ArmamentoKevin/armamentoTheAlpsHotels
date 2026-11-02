package ph.edu.comteq.armamentothealpshotels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// AndroidViewModel providing state and Room Database operations
class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val bookingDao = AppDatabase.getDatabase(application).bookingDao()

    val allBookings: StateFlow<List<Booking>> = bookingDao.getAllBookings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insertBooking(booking: Booking, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            bookingDao.insertBooking(booking)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}

// RunViewModel.kt
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runpath.database.CircuitDAO
import com.example.runpath.database.CircuitRatingDAO
import com.example.runpath.database.LeaderboardDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.example.runpath.models.Leaderboard

class RunViewModel(
    private val circuitRatingDAO: CircuitRatingDAO = CircuitRatingDAO(),
    private val circuitDAO: CircuitDAO = CircuitDAO()
) : ViewModel() {

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Success state
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    private val leaderboardDAO = LeaderboardDAO()
    private val _leaderboardEntries = mutableStateOf<List<Leaderboard>>(emptyList())

    val leaderboardEntries: State<List<Leaderboard>>
        get() = _leaderboardEntries

    // Add new state variables
    private val _isSavingLeaderboard = mutableStateOf(false)
    val isSavingLeaderboard: State<Boolean> = _isSavingLeaderboard
    fun loadLeaderboard(circuitId: String) {
        leaderboardDAO.getCircuitLeaderboard(circuitId) { entries ->
            _leaderboardEntries.value = entries
        }
    }
    fun saveLeaderboardEntry(
        circuitId: String,
        userId: String,
        time: Long,
        distance: Double
    ) {
        _isSavingLeaderboard.value = true
        val entry = Leaderboard(
            circuitId = circuitId,
            userId = userId,
            time = time,
            distance = distance,
            timestamp = System.currentTimeMillis()
        )

        leaderboardDAO.insertLeaderboardEntry(entry) { error ->
            _isSavingLeaderboard.value = false
            if (error != null) {
                // Handle error
            }
        }
    }
    fun saveCircuitRating(
        circuitId: String,
        userId: String,
        intensity: Int,
        lightLevel: Int,
        difficulty: Int
    ) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                circuitRatingDAO.saveCircuitRating(
                    circuitId = circuitId,
                    userId = userId,
                    intensity = intensity,
                    lightLevel = lightLevel,
                    difficulty = difficulty,
                    onComplete = { ratingId ->
                        circuitDAO.updateCircuitWithMedianRatings(circuitId)
                        _isLoading.value = false
                        _isSuccess.value = true
                    },
                    onError = { exception ->
                        _isLoading.value = false
                        _errorMessage.value = "Rating failed: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Unexpected error: ${e.message}"
            }
        }
    }

    fun clearStates() {
        _isSuccess.value = false
        _errorMessage.value = null
    }
}
// RunViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runpath.database.CircuitDAO
import com.example.runpath.database.CircuitRatingDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
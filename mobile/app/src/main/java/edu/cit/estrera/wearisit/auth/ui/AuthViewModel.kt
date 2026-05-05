package edu.cit.estrera.wearisit.auth.ui

import androidx.lifecycle.*
import edu.cit.estrera.wearisit.core.Result
import edu.cit.estrera.wearisit.core.local.TokenManager
import edu.cit.estrera.wearisit.auth.models.AuthResponse
import edu.cit.estrera.wearisit.profile.ProfileResponse
import edu.cit.estrera.wearisit.auth.AuthRepository
import kotlinx.coroutines.launch

enum class AuthScreen {
    LOGIN, REGISTER, PROFILE
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _currentScreen = MutableLiveData(AuthScreen.LOGIN)
    val currentScreen: LiveData<AuthScreen> = _currentScreen

    private val _profile = MutableLiveData<ProfileResponse?>()
    val profile: LiveData<ProfileResponse?> = _profile

    private val _authResponse = MutableLiveData<AuthResponse?>()
    val authResponse: LiveData<AuthResponse?> = _authResponse

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    fun showLogin() {
        _currentScreen.value = AuthScreen.LOGIN
    }

    fun showRegister() {
        _currentScreen.value = AuthScreen.REGISTER
    }

    fun showProfile() {
        _currentScreen.value = AuthScreen.PROFILE
        getProfile()
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            when (val result = authRepository.login(username, password)) {
                is Result.Success -> {
                    val auth = result.data
                    _authResponse.value = auth
                    _successMessage.value = "Login successful"
                    showProfile()
                }
                is Result.Error -> {
                    _error.value = result.message
                }
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            when (val result = authRepository.register(username, email, password)) {
                is Result.Success -> {
                    _authResponse.value = result.data
                    _successMessage.value = "Registration successful"
                }
                is Result.Error -> {
                    _error.value = result.message
                }
            }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            when (val result = authRepository.getProfile()) {
                is Result.Success -> {
                    _profile.value = result.data
                }
                is Result.Error -> {
                    _error.value = result.message
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _profile.value = null
        _authResponse.value = null
        showLogin()
    }

    fun checkForToken() {
        if (tokenManager.getAccessToken() != null) {
            showProfile()
        } else {
            showLogin()
        }
    }
}
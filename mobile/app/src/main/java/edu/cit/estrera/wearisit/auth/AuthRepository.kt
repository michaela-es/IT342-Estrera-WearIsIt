package edu.cit.estrera.wearisit.auth

import edu.cit.estrera.wearisit.auth.models.AuthResponse
import edu.cit.estrera.wearisit.auth.models.LoginRequest
import edu.cit.estrera.wearisit.auth.models.RegisterRequest
import edu.cit.estrera.wearisit.core.network.ApiService
import edu.cit.estrera.wearisit.core.local.TokenManager
import edu.cit.estrera.wearisit.core.network.unwrap
import edu.cit.estrera.wearisit.core.Result
import edu.cit.estrera.wearisit.profile.ProfileResponse

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(usernameOrEmail: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(usernameOrEmail, password))
            val auth = response.unwrap()

            auth.accessToken?.let { tokenManager.saveAccessToken(it) }
            auth.refreshToken?.let { tokenManager.saveRefreshToken(it) }

            Result.Success(auth)

        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(username, email, password))
            val auth = response.unwrap()

            auth.accessToken?.let { tokenManager.saveAccessToken(it) }
            auth.refreshToken?.let { tokenManager.saveRefreshToken(it) }

            Result.Success(auth)

        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed")
        }
    }

    suspend fun getProfile(): Result<ProfileResponse> {
        return try {
            val response = api.getProfile()
            Result.Success(response.unwrap())

        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load profile")
        }
    }

    fun logout() {
        tokenManager.clear()
    }

    fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }
}
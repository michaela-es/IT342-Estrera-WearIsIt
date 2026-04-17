package edu.cit.estrera.wearisit.data.repository

import edu.cit.estrera.wearisit.data.remote.ApiService
import edu.cit.estrera.wearisit.data.local.TokenManager
import edu.cit.estrera.wearisit.data.models.*
import edu.cit.estrera.wearisit.data.remote.unwrap
import edu.cit.estrera.wearisit.core.Result

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
package edu.cit.estrera.wearisit.data.repository

import edu.cit.estrera.wearisit.data.remote.ApiService
import edu.cit.estrera.wearisit.data.local.TokenManager
import edu.cit.estrera.wearisit.data.models.*

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(usernameOrEmail: String, password: String): AuthResponse {
        val response = api.login(LoginRequest(usernameOrEmail, password))
        response.accessToken?.let { tokenManager.saveAccessToken(it) }
        response.refreshToken?.let { tokenManager.saveRefreshToken(it) }
        return response
    }

    suspend fun getProfile(): ProfileResponse {
        return api.getProfile()
    }

    fun logout() {
        tokenManager.clear()
    }

    suspend fun register(username: String, email: String, password: String): AuthResponse {
        val response = api.register(RegisterRequest(username, email, password))
        response.accessToken?.let { tokenManager.saveAccessToken(it) }
        response.refreshToken?.let { tokenManager.saveRefreshToken(it) }
        return response
    }
    fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }
}
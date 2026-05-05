package edu.cit.estrera.wearisit.core.network

import edu.cit.estrera.wearisit.auth.models.AuthResponse
import edu.cit.estrera.wearisit.auth.models.LoginRequest
import edu.cit.estrera.wearisit.profile.ProfileResponse
import edu.cit.estrera.wearisit.auth.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthResponse>>

    @GET("user/me")
    suspend fun getProfile(): Response<ApiResponse<ProfileResponse>>
}
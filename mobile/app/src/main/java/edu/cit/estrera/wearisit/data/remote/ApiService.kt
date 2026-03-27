package edu.cit.estrera.wearisit.data.remote

import edu.cit.estrera.wearisit.data.models.AuthResponse
import edu.cit.estrera.wearisit.data.models.LoginRequest
import edu.cit.estrera.wearisit.data.models.ProfileResponse
import edu.cit.estrera.wearisit.data.models.RegisterRequest
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("user/me")
    suspend fun getProfile(): ProfileResponse
}
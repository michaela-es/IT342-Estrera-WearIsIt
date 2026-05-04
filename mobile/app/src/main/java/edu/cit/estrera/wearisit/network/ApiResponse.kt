package edu.cit.estrera.wearisit.data.remote

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ApiError?,
    val timestamp: String
)
package edu.cit.estrera.wearisit.data.remote

import retrofit2.Response
import com.google.gson.Gson

fun <T> Response<ApiResponse<T>>.unwrap(): T {
    if (!isSuccessful) {
        val errorBody = errorBody()?.string()
        if (errorBody != null) {
            val apiResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
            val message = apiResponse.error?.message
            if (message != null) {
                throw Exception(message)
            }
        }
        throw Exception("Authentication failed")
    }

    val body = body()
    if (body != null && !body.success) {
        throw Exception(body.error?.message ?: "Unknown error")
    }

    return body?.data ?: throw Exception("No data")
}
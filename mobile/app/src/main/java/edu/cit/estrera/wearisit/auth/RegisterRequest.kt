package edu.cit.estrera.wearisit.data.models

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
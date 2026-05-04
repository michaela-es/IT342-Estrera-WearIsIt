package edu.cit.estrera.wearisit.data.models

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)
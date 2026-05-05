package edu.cit.estrera.wearisit.auth.models

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)
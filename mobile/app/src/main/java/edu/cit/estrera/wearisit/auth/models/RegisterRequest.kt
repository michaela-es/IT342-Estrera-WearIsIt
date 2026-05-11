package edu.cit.estrera.wearisit.auth.models

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
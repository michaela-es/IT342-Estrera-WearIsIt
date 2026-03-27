package edu.cit.estrera.wearisit.data.models

data class LoginRequest(
    var usernameOrEmail: String? = null,
    var password: String? = null
)
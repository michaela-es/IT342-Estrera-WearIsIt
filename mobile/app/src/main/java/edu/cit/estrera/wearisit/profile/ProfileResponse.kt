package edu.cit.estrera.wearisit.profile

data class ProfileResponse(
    var username: String? = null,
    var enabled: Boolean? = null,
    var email: String? = null
)
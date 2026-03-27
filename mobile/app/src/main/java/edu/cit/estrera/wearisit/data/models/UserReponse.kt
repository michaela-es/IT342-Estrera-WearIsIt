package edu.cit.estrera.wearisit.data.models

data class UserResponse(
    var user_id: Long? = null,
    var username: String? = null,
    var email: String? = null,
    var isActive: Boolean? = null,
    var enabled: Boolean? = null
)
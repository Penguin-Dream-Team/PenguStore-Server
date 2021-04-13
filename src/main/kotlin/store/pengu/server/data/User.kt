package store.pengu.server.data

import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
data class User(
    val username: String,
    val email: String?,
    val guest: Boolean = email.isNullOrBlank()
)
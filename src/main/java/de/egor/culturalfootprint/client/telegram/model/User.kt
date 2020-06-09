package de.egor.culturalfootprint.client.telegram.model

data class User(
    val chatId: Long,
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null
)

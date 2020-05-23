package de.egor.culturalfootprint.client.telegram.model

import org.bson.codecs.pojo.annotations.BsonId
import java.util.UUID

data class UserEntity(
    @BsonId val id: UUID = UUID.randomUUID(),
    val chatId: Long,
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null
)

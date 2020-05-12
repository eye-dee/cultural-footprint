package de.egor.culturalfootprint.client.telegram.model

import org.bson.codecs.pojo.annotations.BsonId
import java.util.UUID

data class UserEntity(
    @BsonId val id: UUID,
    val chatId: Long,
    val firstName: String?,
    val lastName: String?,
    val username: String?
)

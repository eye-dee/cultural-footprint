package de.egor.culturalfootprint.client.telegram.model

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class UserEntity(
    @BsonId val id: Id<UserEntity>? = null,
    val chatId: Long,
    val firstName: String?,
    val lastName: String?,
    val username: String?
)

package de.egor.culturalfootprint.client.telegram.repository

import com.mongodb.client.model.UpdateOptions
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    db: CoroutineDatabase
) {

    private val collection: CoroutineCollection<UserEntity> = db.getCollection("users")

    fun findAll() = collection.find()

    suspend fun updateOrInsert(userEntity: UserEntity) =
        collection.updateOne(
            UserEntity::chatId eq userEntity.chatId,
            userEntity,
            UpdateOptions().upsert(true)
        )
}

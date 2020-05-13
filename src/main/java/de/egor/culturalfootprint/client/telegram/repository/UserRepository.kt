package de.egor.culturalfootprint.client.telegram.repository

import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setOnInsert
import org.litote.kmongo.setValue
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    db: CoroutineDatabase,
    properties: UserRepositoryProperties
) {

    private val collection: CoroutineCollection<UserEntity> = db.getCollection(properties.collection)

    fun findAll(): Flow<UserEntity> = collection.find().publisher.asFlow()

    suspend fun updateOrInsert(userEntity: UserEntity): UserEntity? =
        collection.findOneAndUpdate(
            UserEntity::chatId eq userEntity.chatId,
            and(
                setValue(UserEntity::firstName, userEntity.firstName),
                setValue(UserEntity::lastName, userEntity.lastName),
                setValue(UserEntity::username, userEntity.username),
                setOnInsert(UserEntity::id, userEntity.id)
            ),
            FindOneAndUpdateOptions()
                .upsert(true)
                .returnDocument(ReturnDocument.AFTER)
        )
}

@Component
@ConfigurationProperties(prefix = "users")
data class UserRepositoryProperties(
    var collection: String = "Users"
)

package de.egor.culturalfootprint.client.telegram.service

import com.elbekD.bot.types.Message
import de.egor.culturalfootprint.client.telegram.converter.Converter
import de.egor.culturalfootprint.client.telegram.model.User
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import de.egor.culturalfootprint.client.telegram.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class UserService(
        private val userRepository: UserRepository,
        private val messageToModelConverter: Converter<Message, UserEntity>,
        private val userToModelConverter: Converter<User, UserEntity>
) {

    fun findAll(): Flow<UserEntity> = userRepository.findAll()

    suspend fun handleUser(user: Message) =
        user.let { messageToModelConverter.convert(it) }
            .let { userRepository.updateOrInsert(it) }

    suspend fun findByTelegramId(telegramUserId: Long)
        = userRepository.findByTelegramId(telegramUserId)

    suspend fun create(user: User): UserEntity =
            userToModelConverter.convert(user).let {
                userRepository.updateOrInsert(it) ?: throw RuntimeException("User upsert returned null")
            }


}

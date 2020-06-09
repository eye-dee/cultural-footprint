package de.egor.culturalfootprint.client.telegram.converter

import de.egor.culturalfootprint.client.telegram.model.User
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import org.springframework.stereotype.Component

@Component
class UserToUserEntityConverter : Converter<User, UserEntity> {
    override fun convert(source: User): UserEntity =
        UserEntity(
            chatId = source.chatId,
            firstName = source.firstName,
            lastName = source.lastName,
            username = source.username
        )
}

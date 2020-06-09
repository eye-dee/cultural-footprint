package de.egor.culturalfootprint.client.telegram.converter

import com.elbekD.bot.types.CallbackQuery
import de.egor.culturalfootprint.client.telegram.model.User
import org.springframework.stereotype.Component

@Component
class TelegramCallbackFromToModelConverter : Converter<CallbackQuery, User> {
    override fun convert(source: CallbackQuery): User =
        User(
            chatId = source.from.id.toLong(),
            firstName = source.from.first_name,
            lastName = source.from.last_name,
            username = source.from.username
        )
}

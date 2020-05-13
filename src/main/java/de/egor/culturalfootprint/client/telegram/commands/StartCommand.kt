package de.egor.culturalfootprint.client.telegram.commands

import com.elbekD.bot.Bot
import com.elbekD.bot.http.await
import com.elbekD.bot.types.Message
import de.egor.culturalfootprint.client.telegram.service.UserService
import org.springframework.stereotype.Component

@Component
class StartCommand(
    private val bot: Bot,
    private val userService: UserService
) : TelegramCommand {

    override fun command(): String = "/start"

    override suspend fun action(message: Message, argument: String?) {
        userService.handleUser(message)
        bot.sendMessage(message.chat.id, "Hello!").await()
    }

}

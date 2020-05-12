package de.egor.culturalfootprint.client.telegram.commands

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import org.springframework.stereotype.Component

@Component
class StartCommand(
    private val bot: Bot
) : TelegramCommand {

    override fun command(): String = "/start"

    override suspend fun action(message: Message, argument: String?) {
        bot.sendMessage(message.chat.id, "Hello!")
    }

}

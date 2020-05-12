package de.egor.culturalfootprint.client.telegram.commands

import com.elbekD.bot.types.Message

interface TelegramCommand {

    fun command(): String

    suspend fun action(message: Message, argument: String?): Unit
}

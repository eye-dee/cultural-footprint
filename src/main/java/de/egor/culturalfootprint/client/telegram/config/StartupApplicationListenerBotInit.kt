package de.egor.culturalfootprint.client.telegram.config

import com.elbekD.bot.Bot
import de.egor.culturalfootprint.client.telegram.commands.TelegramCommand
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class StartupApplicationListenerBotInit(
    private val telegramBot: Bot,
    commands: List<TelegramCommand>
) : ApplicationListener<ContextRefreshedEvent> {

    private val commandsMap = commands.map { it.command() to it::action }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        telegramBot
            .also { bot ->
                commandsMap
                    .forEach { pair ->
                        bot.onCommand(pair.first, pair.second)
                    }
            }
            .also { bot -> bot.start() }
    }
}

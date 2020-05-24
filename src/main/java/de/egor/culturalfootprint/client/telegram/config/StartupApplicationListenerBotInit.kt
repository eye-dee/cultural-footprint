package de.egor.culturalfootprint.client.telegram.config

import com.elbekD.bot.Bot
import de.egor.culturalfootprint.client.telegram.callbacks.CallbackDataFactory
import de.egor.culturalfootprint.client.telegram.commands.TelegramCommand
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class StartupApplicationListenerBotInit(
    private val telegramBot: Bot,
    commands: List<TelegramCommand>,
    private val callbackDataFactory: CallbackDataFactory
) : ApplicationListener<ContextRefreshedEvent> {

    private val commandsMap = commands.map { it.command() to it::action }
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        telegramBot
            .also { bot ->
                commandsMap
                    .forEach { pair ->
                        bot.onCommand(pair.first, pair.second)
                    }
            }
            .also { bot ->
                bot.onCallbackQuery {callbackQuery ->
                    try {
                        callbackQuery.data?.let { data ->
                            val action = callbackDataFactory.parse(data)
                                .execute(callbackQuery.from.id)
                            action(bot)
                        }
                    } catch (e: RuntimeException) {
                        log.warn("Error handling callback query", e)
                    }
                }
            }
            .also { bot -> bot.start() }
    }
}

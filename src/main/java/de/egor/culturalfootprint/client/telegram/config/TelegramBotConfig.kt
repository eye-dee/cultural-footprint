package de.egor.culturalfootprint.client.telegram.config

import com.elbekD.bot.Bot
import de.egor.culturalfootprint.client.telegram.properties.TelegramProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TelegramBotConfig {

    @Bean
    open fun telegramBot(telegramProperties: TelegramProperties): Bot =
        Bot.createPolling(telegramProperties.botUsername, telegramProperties.token)
}

package de.egor.culturalfootprint.client.telegram.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "telegram")
class TelegramProperties {

    lateinit var botUsername: String
    lateinit var token: String
    lateinit var channelName: String
}

package de.egor.culturalfootprint.client.telegram.callbacks

import com.elbekD.bot.Bot
import de.egor.culturalfootprint.client.telegram.markup.LikeMarkupFactory
import de.egor.culturalfootprint.client.telegram.model.User
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import de.egor.culturalfootprint.client.telegram.properties.TelegramProperties
import de.egor.culturalfootprint.client.telegram.service.UserService
import de.egor.culturalfootprint.service.ClusterService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.UUID

class LikeCallbackData(
    val clusterId: UUID,
    userService: UserService,
    clusterService: ClusterService,
    telegramProperties: TelegramProperties,
    likeMarkupFactory: LikeMarkupFactory
) : AbstractLikingCallbackData(clusterService, telegramProperties, likeMarkupFactory, userService) {

    override suspend fun execute(user: User): (Bot) -> Unit {
        val userEntity = findUser(user)
        clusterService.likedBy(clusterId, userEntity)
        return updateLikingMarkup(clusterId)
    }

}

class DislikeCallbackData(
    val clusterId: UUID,
    userService: UserService,
    clusterService: ClusterService,
    telegramProperties: TelegramProperties,
    likeMarkupFactory: LikeMarkupFactory
) : AbstractLikingCallbackData(clusterService, telegramProperties, likeMarkupFactory, userService) {

    override suspend fun execute(user: User): (Bot) -> Unit {
        val userEntity = findUser(user)
        clusterService.dislikedBy(clusterId, userEntity)
        return updateLikingMarkup(clusterId)
    }
}

abstract class AbstractLikingCallbackData(
    val clusterService: ClusterService,
    private val telegramProperties: TelegramProperties,
    private val likeMarkupFactory: LikeMarkupFactory,
    private val userService: UserService
) : CallbackData {

    internal fun updateLikingMarkup(clusterId: UUID): (Bot) -> Unit {
        return { bot ->
            GlobalScope.launch {
                clusterService.findById(clusterId)?.let {
                    bot.editMessageReplyMarkup(
                        chatId = telegramProperties.channelName,
                        messageId = it.telegramPostId,
                        markup = likeMarkupFactory.likingKeyboard(clusterId)
                    ).whenComplete { _, e -> e?.also {
                        log.warn("Exception updating post for cluster {}", clusterId, e)
                    }}
                }
            }
        }
    }

    internal suspend fun findUser(user: User): UserEntity {
        return userService.findByTelegramId(user.chatId) ?: userService.create(user)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}

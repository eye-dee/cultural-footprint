package de.egor.culturalfootprint.client.telegram.service

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import de.egor.culturalfootprint.client.telegram.markup.LikeMarkupFactory
import de.egor.culturalfootprint.client.telegram.properties.TelegramProperties
import de.egor.culturalfootprint.service.ClusterService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Service
class PublisherService(
    private val clusterService: ClusterService,
    private val messageBuilder: MessageBuilder,
    private val bot: Bot,
    private val likeMarkupFactory: LikeMarkupFactory,
    private val telegramProperties: TelegramProperties
) {

    private val log = LoggerFactory.getLogger(PublisherService::class.java)
    private val context = newFixedThreadPoolContext(2, "cluster-publisher")

    suspend fun publishClusterForAllUsers(clusterId: UUID, preview: Boolean) {
        GlobalScope.launch(context) {
            (clusterId.takeIf { preview }?.let { true } ?: clusterService.publish(clusterId).takeIf { it })
                ?.let {
                    clusterService.findApprovedDataFor(clusterId)
                        ?.let { cluster -> messageBuilder.buildMessage(cluster) }
                        ?.also { messages ->
                            messages.subList(0, messages.lastIndex).map { message ->
                                sendMessage(message, clusterId, false, preview).whenComplete { _, ex ->
                                    logPublishingError(ex, clusterId)
                                }
                            }
                            sendMessage(messages.last(), clusterId, !preview, preview).whenComplete { post, ex ->
                                logPublishingError(ex, clusterId)
                                post?.takeIf { !preview }?.also {
                                    GlobalScope.launch {
                                        clusterService.updateTelegramPostId(clusterId, post.message_id)
                                    }
                                }
                            }
                        }

                }
        }
    }

    private fun logPublishingError(ex: Throwable?, clusterId: UUID) {
        ex?.also {
            log.warn("Exception occurred during publishing cluster {}",
                clusterId, it)
        }
    }

    private suspend fun sendMessage(message: String, clusterId: UUID, likesMarkup: Boolean, preview: Boolean):
        CompletableFuture<Message> {
        return bot.sendMessage(
            chatId = telegramProperties.previewChannelName.takeIf { preview } ?: telegramProperties.channelName,
            text = message,
            parseMode = "Markdown",
            disableWebPagePreview = true,
            markup = likeMarkupFactory.takeIf { likesMarkup }?.likingKeyboard(clusterId)
        )
    }
}


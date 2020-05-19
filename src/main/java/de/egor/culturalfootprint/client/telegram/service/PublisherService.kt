package de.egor.culturalfootprint.client.telegram.service

import com.elbekD.bot.Bot
import de.egor.culturalfootprint.service.ClusterService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PublisherService(
    private val userService: UserService,
    private val clusterService: ClusterService,
    private val messageBuilder: MessageBuilder,
    private val bot: Bot
) {

    private val log = LoggerFactory.getLogger(PublisherService::class.java)
    private val context = newFixedThreadPoolContext(2, "cluster-publisher")

    suspend fun publishClusterForAllUsers(clusterId: UUID) {
        GlobalScope.launch(context) {
            clusterService.publish(clusterId).takeIf { it }
                ?.let {
                    clusterService.findApprovedDataFor(clusterId)
                        ?.let { cluster -> messageBuilder.buildMessage(cluster) }
                        ?.also { message ->
                            bot.sendMessage(
                                chatId = "cultural_footprint",
                                text = message,
                                parseMode = "Markdown",
                                disableWebPagePreview = true
                            ).whenComplete { _, ex ->
                                ex?.also {
                                    log.warn("Exception occurred during publishing cluster {}",
                                        clusterId, it)
                                }
                            }
                        }
                }
        }
    }
}

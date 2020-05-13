package de.egor.culturalfootprint.client.telegram.service

import com.elbekD.bot.Bot
import de.egor.culturalfootprint.service.ClusterService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PublisherService(
    private val userService: UserService,
    private val clusterService: ClusterService,
    private val messageBuilder: MessageBuilder,
    private val bot: Bot
) {

    private val context = newFixedThreadPoolContext(2, "cluster-publisher")

    suspend fun publishClusterForAllUsers(clusterId: UUID) {
        GlobalScope.launch(context) {
            clusterService.publish(clusterId).takeIf { it }
                ?.let {
                    clusterService.findApprovedDataFor(clusterId)
                        ?.let { cluster -> messageBuilder.buildMessage(cluster) }
                        ?.also { message ->
                            userService.findAll().collect { user ->
                                bot.sendMessage(user.chatId, message)
                                delay(100)
                            }
                        }
                }
        }
    }
}

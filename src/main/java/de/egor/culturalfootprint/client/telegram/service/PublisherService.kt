package de.egor.culturalfootprint.client.telegram.service

import com.elbekD.bot.Bot
import com.elbekD.bot.http.await
import de.egor.culturalfootprint.service.ClusterService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PublisherService(
    private val userService: UserService,
    private val clusterService: ClusterService,
    private val bot: Bot
) {
    private val context = newFixedThreadPoolContext(2, "cluster-publisher")

    suspend fun publishClusterForAllUsers(clusterId: UUID) {
        GlobalScope.launch (context) {
                clusterService.findClusterById(clusterId)
                    ?.also { cluster ->
                        userService.findAll()
                            .consumeEach { user ->
                                cluster.records
                                    .map {
                                        delay(1000L)
                                        bot.sendMessage(user.chatId, it.data)
                                    }
                                    .forEach { it.await() }
                            }
                    }
            }
        }
}

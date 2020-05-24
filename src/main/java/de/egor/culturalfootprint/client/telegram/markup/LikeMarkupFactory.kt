package de.egor.culturalfootprint.client.telegram.markup

import com.elbekD.bot.types.InlineKeyboardButton
import com.elbekD.bot.types.InlineKeyboardMarkup
import de.egor.culturalfootprint.client.telegram.callbacks.CallbackDataFactory
import de.egor.culturalfootprint.repository.ClusterRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class LikeMarkupFactory(
    private val clusterRepository: ClusterRepository,
    private val callbackDataFactory: CallbackDataFactory
) {

    suspend fun likingKeyboard(clusterId: UUID): InlineKeyboardMarkup? =
        clusterRepository.findClusterById(clusterId)?.let { cluster ->
            InlineKeyboardMarkup(
                listOf(listOf(
                    InlineKeyboardButton(
                        text = "\uD83D\uDC4D ${cluster.likedBy.size}",
                        callback_data = callbackDataFactory.likeCallbackData(clusterId)
                            .toCallbackString()
                    ),
                    InlineKeyboardButton(
                        text = "\uD83D\uDC4E ${cluster.dislikedBy.size}",
                        callback_data = callbackDataFactory.dislikeCallbackData(clusterId)
                            .toCallbackString()
                    )
                ))
            )
        }


}

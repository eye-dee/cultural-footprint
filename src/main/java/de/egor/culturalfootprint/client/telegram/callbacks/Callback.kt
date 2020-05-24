package de.egor.culturalfootprint.client.telegram.callbacks

import com.elbekD.bot.Bot
import de.egor.culturalfootprint.client.telegram.markup.LikeMarkupFactory
import de.egor.culturalfootprint.client.telegram.properties.TelegramProperties
import de.egor.culturalfootprint.client.telegram.service.UserService
import de.egor.culturalfootprint.service.ClusterService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CallbackDataFactory(
    private val userService: UserService,
    private val clusterService: ClusterService,
    private val likeMarkupFactory: LikeMarkupFactory,
    private val telegramProperties: TelegramProperties
) {

    private val callbackDataCreators = mapOf<CallbackType, (List<String>) -> CallbackData>(
        Pair(CallbackType.LIKE) { elements: List<String> ->
            LikeCallbackData(
                clusterId = UUID.fromString(elements[1]),
                userService = userService,
                clusterService = clusterService,
                likeMarkupFactory = likeMarkupFactory,
                telegramProperties = telegramProperties
            )
        },
        Pair(CallbackType.DISLIKE) { elements: List<String> ->
            DislikeCallbackData(
                clusterId = UUID.fromString(elements[1]),
                userService = userService,
                clusterService = clusterService,
                likeMarkupFactory = likeMarkupFactory,
                telegramProperties = telegramProperties
            )
        }
    )

    fun parse(callbackString: String): CallbackData {
        if (callbackString.isBlank()) {
            throw CallbackDataParsingException("Callback string is blank")
        }
        val splitCallback: List<String> = callbackString.split(delimiter)
        val callbackType = CallbackType.parse(splitCallback[0])
            ?: throw CallbackDataParsingException("Unknown callback type '${splitCallback[0]}'")
        return callbackDataCreators[callbackType]?.invoke(splitCallback)
            ?: throw CallbackDataParsingException("No creator for callback type '$callbackType'")
    }

    fun likeCallbackData(clusterId: UUID): LikeCallbackData
        = LikeCallbackData(clusterId, userService, clusterService, telegramProperties, likeMarkupFactory)

    fun dislikeCallbackData(clusterId: UUID): DislikeCallbackData
        = DislikeCallbackData(clusterId, userService, clusterService, telegramProperties, likeMarkupFactory)

    companion object {
        const val delimiter = ":"
    }

}

interface CallbackData {

    fun toCallbackString(): String

    suspend fun execute(telegramUserId: Int): (Bot) -> Unit

}

enum class CallbackType(val prefix: String) {
    LIKE("L"),
    DISLIKE("DL");

    companion object Parser {
        private val typeByPrefix: Map<String, CallbackType> = values()
            .map { Pair(it.prefix, it) }
            .toMap()

        fun parse(prefix: String): CallbackType? = typeByPrefix[prefix]
    }
}

class CallbackDataParsingException(message: String) : RuntimeException(message)

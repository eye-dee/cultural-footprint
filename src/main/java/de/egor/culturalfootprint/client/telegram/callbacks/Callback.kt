package de.egor.culturalfootprint.client.telegram.callbacks

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CallbackDataFactory {

    private val callbackDataCreators = mapOf<CallbackType, (List<String>) -> CallbackData>(
        Pair(CallbackType.LIKE) {
            elements: List<String> -> LikeCallbackData(clusterId = UUID.fromString(elements[1]))
        },
        Pair(CallbackType.DISLIKE) {
            elements: List<String> ->  DislikeCallbackData(clusterId = UUID.fromString(elements[1]))
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

    companion object {
        private const val delimiter = ":"
    }

}

interface CallbackData

enum class CallbackType(val prefix: String) {
    LIKE("L"),
    DISLIKE("DL");

    companion object Parser {
        private val typeByPrefix: Map<String, CallbackType> = values()
            .map { Pair(it.prefix, it) }
            .toMap()

        fun parse(prefix: String): CallbackType? {
            return typeByPrefix[prefix]
        }
    }
}

class CallbackDataParsingException(message: String) : RuntimeException(message)

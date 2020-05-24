package de.egor.culturalfootprint.client.telegram.callbacks

import java.util.UUID

class LikeCallbackData(val clusterId: UUID) : CallbackData {
}

class DislikeCallbackData(val clusterId: UUID) : CallbackData {
}

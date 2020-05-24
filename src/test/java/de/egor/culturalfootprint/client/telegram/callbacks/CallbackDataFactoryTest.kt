package de.egor.culturalfootprint.client.telegram.callbacks

import de.egor.culturalfootprint.client.telegram.markup.LikeMarkupFactory
import de.egor.culturalfootprint.client.telegram.properties.TelegramProperties
import de.egor.culturalfootprint.client.telegram.service.UserService
import de.egor.culturalfootprint.service.ClusterService
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.util.UUID

internal class CallbackDataFactoryTest {

    private val userService = mock(UserService::class.java)
    private val clusterService = mock(ClusterService::class.java)
    private val likeMarkupFactory = mock(LikeMarkupFactory::class.java)
    private val callbackDataFactory = CallbackDataFactory(
        userService = userService,
        clusterService = clusterService,
        telegramProperties = TelegramProperties(),
        likeMarkupFactory = likeMarkupFactory
    )

    @Test
    internal fun `should throw exception if string is blank`() {
        Assertions.assertThatThrownBy { callbackDataFactory.parse("  ") }
            .isInstanceOf(CallbackDataParsingException::class.java)
            .hasMessageContaining("blank")
    }

    @Test
    internal fun `should throw exception if callback type is unknown`() {
        Assertions.assertThatThrownBy { callbackDataFactory.parse("UNKNOWN:TEST") }
            .isInstanceOf(CallbackDataParsingException::class.java)
            .hasMessageContaining("type")
    }

    @Test
    internal fun `should parse like callback`() {
        val clusterId = UUID.randomUUID()

        val callbackData = callbackDataFactory.parse("L:$clusterId")

        assertThat(callbackData).isInstanceOf(LikeCallbackData::class.java)
        assertThat((callbackData as LikeCallbackData).clusterId).isEqualTo(clusterId)
    }

    @Test
    internal fun `should parse dislike callback`() {
        val clusterId = UUID.randomUUID()

        val callbackData = callbackDataFactory.parse("DL:$clusterId")

        assertThat(callbackData).isInstanceOf(DislikeCallbackData::class.java)
        assertThat((callbackData as DislikeCallbackData).clusterId).isEqualTo(clusterId)
    }
}

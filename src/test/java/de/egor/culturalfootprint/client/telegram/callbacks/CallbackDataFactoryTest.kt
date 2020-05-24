package de.egor.culturalfootprint.client.telegram.callbacks

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

internal class CallbackDataFactoryTest {

    private val callbackDataFactory = CallbackDataFactory()

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

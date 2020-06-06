package de.egor.culturalfootprint.client.telegram.service

import de.egor.culturalfootprint.admin.dto.ClusterResult
import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.model.RawRecord
import de.egor.culturalfootprint.model.RecordSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

internal class MessageBuilderTest {

    private val messageBuilder = MessageBuilder()

    @Test
    internal fun windowedMessages() {
        val clusterResult = ClusterResult(
            cluster = Cluster(
                id = UUID.randomUUID(),
                week = "2020-23",
                name = "Cluster name"
            ),
            records = (1..30).map {
                RawRecord(
                    id = UUID.randomUUID(),
                    week = "2020-23",
                    data = "data$it",
                    source = RecordSource(
                        tweetId = 100
                    ),
                    date = LocalDateTime.now()
                )
            }
        )


        val messages = messageBuilder.buildMessage(clusterResult)

        assertThat(messages).hasSize(2)
        assertThat(messages[0]).contains("Часть 1")
        (1..20).forEach {
            assertThat(messages[0]).contains("data$it")
        }
        assertThat(messages[1]).contains("Часть 2")
        (21..30).forEach {
            assertThat(messages[1]).contains("data$it")
        }
    }
}

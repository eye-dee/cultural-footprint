package de.egor.culturalfootprint.record.collector

import de.egor.culturalfootprint.record.RawRecordService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDateTime
import java.time.ZoneId

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TwitterCollectorTest {

    @Autowired
    private lateinit var collector: TwitterCollector

    @MockBean
    private lateinit var rawRecordService: RawRecordService

    @Test
    fun getRecords() {
        val records = collector.getRecords()

        assertThat(records).isNotEmpty
        val rawRecord = records[0]
        assertThat(rawRecord.data).isNotBlank()
        assertThat(rawRecord.source.tweetId).isNotNegative()
        assertThat(rawRecord.date).isBefore(LocalDateTime.now(ZoneId.of("UTC")))
    }

}

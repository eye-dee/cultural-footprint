package de.egor.culturalfootprint.record.collector

import de.egor.culturalfootprint.record.repository.RawRecordRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import twitter4j.RateLimitStatus
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.Twitter
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class TwitterCollectorTest {

    @Mock
    private lateinit var twitter: Twitter

    @Mock
    private lateinit var collectorProperties: TwitterCollectorProperties

    @Mock
    private lateinit var rawRecordRepository: RawRecordRepository

    @Mock
    private lateinit var status: Status

    @InjectMocks
    private lateinit var collector: TwitterCollector

    @Test
    fun processTweetWithTextAndDate() {
        runBlocking {
            given(rawRecordRepository.getLatestRecordTweetId()).willReturn(Optional.of(10))
            given(collectorProperties.requestedPageSize).willReturn(300)
            given(twitter.getHomeTimeline(any())).willReturn(TwitterResponseList(listOf(status)))
            given(status.id).willReturn(100500)
            given(status.text).willReturn("some text")
            val date = LocalDateTime.of(2019, 2, 13, 15, 0)
            given(status.createdAt).willReturn(Date.from(date.toInstant(ZoneOffset.UTC)))

            val records = collector.getRecords()

            assertThat(records).isNotEmpty
            val rawRecord = records[0]
            assertThat(rawRecord.data).isEqualTo("some text")
            assertThat(rawRecord.source.tweetId).isEqualTo(100500)
            assertThat(rawRecord.date).isEqualTo(date)
        }
    }

    @Test
    fun processTweetWithTextAndNoDate() {
        runBlocking {
            given(rawRecordRepository.getLatestRecordTweetId()).willReturn(Optional.of(10))
            given(collectorProperties.requestedPageSize).willReturn(300)
            given(twitter.getHomeTimeline(any())).willReturn(TwitterResponseList(listOf(status)))
            given(status.id).willReturn(100500)
            given(status.text).willReturn("some text")
            val date = LocalDateTime.of(2019, 2, 13, 15, 0)
            given(status.createdAt).willReturn(Date.from(date.toInstant(ZoneOffset.UTC)))

            val records = collector.getRecords()

            assertThat(records).isNotEmpty
            val rawRecord = records[0]
            assertThat(rawRecord.data).isEqualTo("some text")
            assertThat(rawRecord.source.tweetId).isEqualTo(100500)
            assertThat(rawRecord.date).isBefore(LocalDateTime.now(Clock.systemUTC()))
        }
    }

    @Test
    fun skipTweetWithoutText() {
        runBlocking {
            given(rawRecordRepository.getLatestRecordTweetId()).willReturn(Optional.of(10))
            given(collectorProperties.requestedPageSize).willReturn(300)
            given(twitter.getHomeTimeline(any())).willReturn(TwitterResponseList(listOf(status)))
            given(status.text).willReturn(null)

            val records = collector.getRecords()

            assertThat(records).isEmpty()
        }
    }

    class TwitterResponseList(list: List<Status>) : ArrayList<Status>(list), ResponseList<Status> {
        override fun getRateLimitStatus(): RateLimitStatus {
            throw RuntimeException()
        }

        override fun getAccessLevel(): Int {
            throw RuntimeException()
        }
    }

}

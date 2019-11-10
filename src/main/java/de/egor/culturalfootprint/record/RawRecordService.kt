package de.egor.culturalfootprint.record

import de.egor.culturalfootprint.record.collector.RawRecord
import de.egor.culturalfootprint.record.collector.TwitterCollector
import de.egor.culturalfootprint.record.repository.RawRecordRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import javax.annotation.PostConstruct

@Service
open class RawRecordService(
        private val collector: TwitterCollector,
        private val repository: RawRecordRepository
) {

    private val channel: Channel<List<RawRecord>> = Channel(capacity = 1000)
    private val log = LoggerFactory.getLogger(RawRecordService::class.java)

    @PostConstruct
    fun startPolling() {
        GlobalScope.launch {
            for (records in channel) {
                try {
                    repository.save(records)
                } catch (e: Exception) {
                    log.warn("Exception processing records", e)
                }
            }
        }
        GlobalScope.launch {
            while (true) {
                log.debug("Requesting records")
                val records = collector.getRecords()
                log.debug("Retrieved {} records", records.size)
                channel.send(records)
                delay(Duration.ofHours(1).toMillis())
            }
        }
        log.info("Polling started")
    }
}

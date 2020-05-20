package de.egor.culturalfootprint.record

import de.egor.culturalfootprint.model.RawRecord
import de.egor.culturalfootprint.record.collector.TwitterCollector
import de.egor.culturalfootprint.repository.RawRecordRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID
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
                    log.warn("Exception saving records", e)
                }
            }
        }
        GlobalScope.launch {
            while (true) {
                log.debug("Requesting records")
                try {
                    val records = collector.getRecords()
                    log.debug("Retrieved {} records", records.size)
                    channel.send(records)
                } catch (e: Exception) {
                    log.warn("Exception collecting records", e)
                }
                delay(Duration.ofMinutes(15).toMillis())
            }
        }
        log.info("Polling started")
    }

    suspend fun submitApproval(recordId: UUID): Boolean =
        repository.updateApproval(recordId, true)

    suspend fun withdrawApproval(recordId: UUID): Boolean =
            repository.updateApproval(recordId, false)
}

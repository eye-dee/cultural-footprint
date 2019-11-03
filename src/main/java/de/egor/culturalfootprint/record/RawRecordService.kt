package de.egor.culturalfootprint.record

import de.egor.culturalfootprint.record.collector.RawRecord
import de.egor.culturalfootprint.record.collector.TwitterCollector
import de.egor.culturalfootprint.record.repository.RawRecordRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Service
open class RawRecordService(
    private val collector: TwitterCollector,
    private val repository: RawRecordRepository
) {

    private val scheduledExecutor = Executors.newSingleThreadScheduledExecutor()
    private val executor = Executors.newSingleThreadExecutor()
    private val queue: BlockingQueue<List<RawRecord>> = ArrayBlockingQueue(10)
    private val log = LoggerFactory.getLogger(RawRecordService::class.java)

    @PostConstruct
    fun startPolling() {
        log.info("Polling started")
        scheduledExecutor.scheduleWithFixedDelay({
            log.debug("Requesting records")
            val records = collector.getRecords()
            log.debug("Retrieved {} records", records.size)
            queue.put(records)
        }, 0, 1, TimeUnit.HOURS)
        executor.submit {
            while (true) {
                try {
                    log.debug("Polling the queue")
                    val records = queue.take()
                    log.debug("Processing {} records from queue", records.size)
                    repository.save(records)
                    log.debug("Saved {} records", records.size)
                } catch (e: Exception) {
                    log.warn("Exception processing records", e)
                }
            }
        }
    }
}

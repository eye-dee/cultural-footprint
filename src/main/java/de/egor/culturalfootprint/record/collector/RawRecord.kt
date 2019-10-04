package de.egor.culturalfootprint.record.collector

import java.time.LocalDateTime

data class RawRecord(val title: String? = null, val date: LocalDateTime, val source: RecordSource, val data: String)

data class RecordSource(val tweetId: Long)

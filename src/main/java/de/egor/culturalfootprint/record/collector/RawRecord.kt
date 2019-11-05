package de.egor.culturalfootprint.record.collector

import org.bson.codecs.pojo.annotations.BsonId
import java.time.LocalDateTime
import java.time.temporal.IsoFields
import java.util.*

data class RawRecord(@BsonId val id: UUID,
                     val title: String? = null,
                     val date: LocalDateTime,
                     val source: RecordSource,
                     val data: String,
                     val cluster: UUID? = null,
                     val week: String = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR).toString() + "-" + date.year)

data class RecordSource(val tweetId: Long)

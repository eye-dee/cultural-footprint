package de.egor.culturalfootprint.model

import org.bson.codecs.pojo.annotations.BsonId
import java.time.LocalDateTime
import java.time.temporal.IsoFields
import java.util.UUID

data class RawRecord(
    @BsonId val id: UUID,
    val title: String? = null,
    val date: LocalDateTime,
    val source: RecordSource,
    val data: String,
    val cluster: UUID? = null,
    val week: String = currentWeek(date),
    val approved: Boolean? = false
)

private fun currentWeek(date: LocalDateTime): String {
    val weekAsString = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR).toString()
    val adjustedWeekString = if (weekAsString.length == 1) "0$weekAsString" else weekAsString

    return date.year.toString() + "-" + adjustedWeekString
}

data class RecordSource(
    val tweetId: Long,
    val sourceRepresentation: RecordSourceRepresentation? = null
)

data class RecordSourceRepresentation(val name: String, val url: String?)

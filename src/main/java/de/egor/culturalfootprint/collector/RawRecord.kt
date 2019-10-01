package de.egor.culturalfootprint.collector

import java.time.LocalDateTime

data class RawRecord(val title: String?, val date: LocalDateTime, val source: String, val data: String)
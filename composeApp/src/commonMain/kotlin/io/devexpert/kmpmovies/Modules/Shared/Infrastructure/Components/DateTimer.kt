package io.devexpert.kmpmovies.Modules.Shared.Infrastructure.Components

import kotlinx.datetime.*

object DateTimer {

    fun getTimezone(): String = TimeZone.currentSystemDefault().id

    fun getNowAsTimestamp(): Long = Clock.System.now().toEpochMilliseconds()

    fun getNowYmdHis(): String {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return formatDateTime(localDateTime)
    }

    fun getToday(): String {
        val now = Clock.System.now()
        val localDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
    }

    fun isValidDateYmd(dateString: String): Boolean {
        return try {
            LocalDate.parse(dateString)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getDateYmdHisAsString(timestamp: Long?): String {
        if (timestamp == null) return ""
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return formatDateTime(localDateTime)
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        return "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')} " +
                "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}:${dateTime.second.toString().padStart(2, '0')}"
    }
}

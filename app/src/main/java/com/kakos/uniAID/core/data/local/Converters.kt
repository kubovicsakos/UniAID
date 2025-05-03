package com.kakos.uniAID.core.data.local

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Type converters for Room database persistence.
 *
 * Provides bidirectional conversion between complex Java time types
 * and their String representations for database storage.
 * Handles LocalDate, LocalTime, LocalDateTime, and collections
 * of DayOfWeek objects.
 */
class Converters {
    // Convert LocalDate to String
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    // Convert String to LocalDate
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    // Convert LocalTime to String
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    // Convert String to LocalTime
    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
    }

    // Convert LocalDateTime to String
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.toString()
    }

    // Convert String to LocalDateTime
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it) }
    }

    // Convert List<DayOfWeek> to String
    @TypeConverter
    fun fromDayOfWeekList(dayOfWeekList: List<DayOfWeek>?): String? {
        return dayOfWeekList?.joinToString(separator = ",") { it.name }
    }

    // Convert String to List<DayOfWeek>
    @TypeConverter
    fun toDayOfWeekList(dayOfWeekListString: String?): List<DayOfWeek> {
        return dayOfWeekListString?.split(",")?.mapNotNull { dayOfWeekString ->
            try {
                DayOfWeek.valueOf(dayOfWeekString)
            } catch (e: IllegalArgumentException) {
                null // Handle invalid day of week string
            }
        } ?: emptyList()
    }
}

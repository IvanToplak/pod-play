package hr.from.ivantoplak.podplay.db

import androidx.room.TypeConverter
import java.util.*

class DbConverters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
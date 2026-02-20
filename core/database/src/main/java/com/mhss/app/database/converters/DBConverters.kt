package com.mhss.app.database.converters

import androidx.room.TypeConverter
import com.mhss.app.domain.model.Mood
import com.mhss.app.domain.model.SubTask
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DBConverters {

    @TypeConverter
    fun fromSubTasksList(value: List<SubTask>): String {
        return Json.encodeToString(value)
    }
    @TypeConverter
    fun toSubTasksList(value: String): List<SubTask> {
        val json = Json {
            ignoreUnknownKeys = true
        }
        return json.decodeFromString<List<SubTask>>(value)
    }

    @TypeConverter
    fun toMood(value: Int) = enumValues<Mood>()[value]
    @TypeConverter
    fun fromMood(value: Mood) = value.ordinal

    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return Json.encodeToString(value)
    }
    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val json = Json {
            ignoreUnknownKeys = true
        }
        return json.decodeFromString<List<Int>>(value)
    }

    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return Json.encodeToString(value)
    }
    @TypeConverter
    fun toLongList(value: String): List<Long> {
        val json = Json {
            ignoreUnknownKeys = true
        }
        return json.decodeFromString<List<Long>>(value)
    }
}
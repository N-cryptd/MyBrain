package com.mhss.app.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "note_links")
@Serializable
data class NoteLinkEntity(
    @ColumnInfo(name = "from_note_id")
    @SerialName("fromNoteId")
    val fromNoteId: String,
    
    @ColumnInfo(name = "to_note_id")
    @SerialName("toNoteId")
    val toNoteId: String,
    
    @ColumnInfo(name = "created_date")
    @SerialName("createdDate")
    val createdDate: Long = 0L,
    
    @PrimaryKey
    @SerialName("id")
    val id: String
)
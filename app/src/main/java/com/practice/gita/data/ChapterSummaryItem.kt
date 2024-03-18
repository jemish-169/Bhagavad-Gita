package com.practice.gita.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapter")
data class ChapterSummaryItem(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val chapter_number: Int,
    val chapter_summary: String,
    val name: String,
    val name_meaning: String,
    val name_translated: String
)
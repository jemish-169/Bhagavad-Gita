package com.practice.gita.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verses")
data class VerseItemDB(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val chapter_number: Int = 0,
    val isBookmarked: Boolean = false,
    val text: String = "",
    val transliteration: String = "",
    val verse_number: Int = 0,
    val word_meanings: String = ""
)
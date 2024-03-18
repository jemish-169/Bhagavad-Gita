package com.practice.gita.data

data class VerseForUI(
    val id: Int,
    val chapter_number: Int,
    val isBookmarked: Boolean,
    val text: String,
    val transliteration: String,
    val verse_number: Int,
    val word_meanings: String,
    val translation: Translation,
    val commentary: Commentary
)

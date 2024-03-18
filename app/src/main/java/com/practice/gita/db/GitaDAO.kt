package com.practice.gita.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practice.gita.data.ChapterSummaryItem
import com.practice.gita.data.Commentary
import com.practice.gita.data.Translation
import com.practice.gita.data.VerseItemDB

@Dao
interface GitaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addChapterSummary(chapters: List<ChapterSummaryItem>)

    @Query("SELECT * FROM chapter")
    suspend fun getChapterSummary(): List<ChapterSummaryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVerseList(verses: List<VerseItemDB>)

    @Query("SELECT * FROM verses WHERE chapter_number = :chapter AND verse_number =:verse")
    suspend fun getVerse(chapter: Int, verse: Int): VerseItemDB

    @Query("UPDATE verses SET isBookmarked = :bookmarked WHERE id = :id")
    suspend fun updateVerse(id: Int, bookmarked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCommentary(commentaries: List<Commentary>)

    @Query("SELECT * FROM commentary WHERE author_name = :author AND id BETWEEN :startId AND :endId")
    suspend fun getCommentary(startId: Int, endId: Int, author: String): Commentary

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTranslation(translations: List<Translation>)

    @Query("SELECT * FROM translation WHERE author_name = :author AND id BETWEEN :startId AND :endId")
    suspend fun getTranslation(startId: Int, endId: Int, author: String): Translation

    @Query("SELECT * FROM verses WHERE isBookmarked = :isBookmarked")
    suspend fun getBookmark(isBookmarked: Boolean = true): List<VerseItemDB>
}
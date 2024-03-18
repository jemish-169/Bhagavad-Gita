package com.practice.gita.repository

import android.content.Context
import com.practice.gita.api.ApiService
import com.practice.gita.data.ChapterSummaryItem
import com.practice.gita.data.Commentary
import com.practice.gita.data.Translation
import com.practice.gita.data.VerseForUI
import com.practice.gita.data.VerseItemDB
import com.practice.gita.db.GitaDataBase
import com.practice.gita.utils.Constants.Companion.commentaryNumberList
import com.practice.gita.utils.Constants.Companion.translationNumberList
import com.practice.gita.utils.NetworkUtils.Companion.isInternetAvailable
import com.practice.gita.utils.ResultOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class GitaRepository(
    private val apiService: ApiService,
    private val gitaDataBase: GitaDataBase,
    private val context: Context
) {


    suspend fun getChapterSummary(): ResultOf<List<ChapterSummaryItem>> {
        try {
            val chapterSummaryList = gitaDataBase.gitaDAO.getChapterSummary()
            if (chapterSummaryList.isNotEmpty()) {
                return ResultOf.Success(chapterSummaryList)
            } else if (isInternetAvailable(context)) {
                val result = apiService.getChapterSummary()
                if (result.body() != null) {
                    gitaDataBase.gitaDAO.addChapterSummary(result.body()!!)
                    return ResultOf.Success(result.body()!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ResultOf.Failure(e)
        }
        return ResultOf.Failure(Exception())
    }

    suspend fun getVersesFromInternet(chapter: Int) {
        try {
            if (isInternetAvailable(context)) {
                val result = apiService.getAllVerses(chapter)
                if (result.body() != null) {
                    val verseList = result.body()
                    val verseListDB = mutableListOf<VerseItemDB>()
                    val commentaryListDB = mutableListOf<Commentary>()
                    val translationListDB = mutableListOf<Translation>()
                    verseList!!.forEach { item ->
                        val verseItemDB = VerseItemDB(
                            chapter_number = item.chapter_number,
                            id = item.id,
                            text = item.text,
                            transliteration = item.transliteration,
                            verse_number = item.verse_number,
                            word_meanings = item.word_meanings
                        )
                        commentaryListDB.addAll(item.commentaries)
                        translationListDB.addAll(item.translations)
                        verseListDB.add(verseItemDB)
                    }
                    coroutineScope {
                        launch { gitaDataBase.gitaDAO.addVerseList(verseListDB) }
                        launch { gitaDataBase.gitaDAO.addCommentary(commentaryListDB) }
                        launch { gitaDataBase.gitaDAO.addTranslation(translationListDB) }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getSingleVerseFromInternet(
        chapter: Int,
        verse: Int,
        comSource: String,
        tranSource: String,
    ): ResultOf<VerseForUI> {
        try {
            if (isInternetAvailable(context)) {
                val result = apiService.getSingleVerse(chapter, verse)
                if (result.body() != null) {
                    val verseItem = result.body()
                    val verseListDB = mutableListOf<VerseItemDB>()
                    val commentaryListDB = mutableListOf<Commentary>()
                    val translationListDB = mutableListOf<Translation>()
                    verseItem?.let { item ->
                        val verseItemDB = VerseItemDB(
                            chapter_number = item.chapter_number,
                            id = item.id,
                            text = item.text,
                            transliteration = item.transliteration,
                            verse_number = item.verse_number,
                            word_meanings = item.word_meanings
                        )
                        commentaryListDB.addAll(item.commentaries)
                        translationListDB.addAll(item.translations)
                        verseListDB.add(verseItemDB)
                    }
                    coroutineScope {
                        launch { gitaDataBase.gitaDAO.addVerseList(verseListDB) }
                        launch { gitaDataBase.gitaDAO.addCommentary(commentaryListDB) }
                        launch { gitaDataBase.gitaDAO.addTranslation(translationListDB) }
                    }
                    return getVerseFromDatabase(chapter, verse, comSource, tranSource)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ResultOf.Failure(e)
        }
        return ResultOf.Failure(Exception())
    }

    suspend fun getVerseFromDatabase(
        chapter: Int,
        verse: Int,
        comSource: String,
        tranSource: String,
    ): ResultOf<VerseForUI> {
        try {
            var chapterVerse = VerseItemDB()
            var commentary = Commentary()
            var translation = Translation()
            coroutineScope {
                launch(Dispatchers.IO) { chapterVerse = getVerse(chapter, verse) }
                launch(Dispatchers.IO) { commentary = getCommentary(chapter, verse, comSource) }
                launch(Dispatchers.IO) {
                    translation = getTranslation(chapter, verse, tranSource)
                }
            }
            val result = VerseForUI(
                id = chapterVerse.id,
                chapter_number = chapterVerse.chapter_number,
                isBookmarked = chapterVerse.isBookmarked,
                text = chapterVerse.text,
                transliteration = chapterVerse.transliteration,
                verse_number = chapterVerse.verse_number,
                word_meanings = chapterVerse.word_meanings,
                translation = translation,
                commentary = commentary
            )
            return ResultOf.Success(result)

        } catch (e: Exception) {
            e.printStackTrace()
            return ResultOf.Failure(e)
        }
    }

    private suspend fun getVerse(chapter: Int, verse: Int): VerseItemDB {
        return try {
            coroutineScope { async { gitaDataBase.gitaDAO.getVerse(chapter, verse) }.await() }
        } catch (e: Exception) {
            VerseItemDB()
        }
    }

    private suspend fun getCommentary(chapter: Int, verse: Int, comSource: String): Commentary {
        val comRangeBetween =
            if (chapter == 2 && verse > 42)
                1425 + ((verse - 43) * 15)
            else
                commentaryNumberList[chapter - 1] + ((verse - 1) * 16)
        return try {
            coroutineScope {
                async {
                    gitaDataBase.gitaDAO.getCommentary(
                        comRangeBetween, comRangeBetween + 15, comSource
                    )
                }.await()
            }
        } catch (e: Exception) {
            Commentary()
        }
    }

    private suspend fun getTranslation(chapter: Int, verse: Int, tranSource: String): Translation {
        val traRangeBetween = translationNumberList[chapter - 1] + ((verse - 1) * 7)
        return try {
            coroutineScope {
                async {
                    gitaDataBase.gitaDAO.getTranslation(
                        traRangeBetween, traRangeBetween + 6, tranSource
                    )
                }.await()
            }
        } catch (e: Exception) {
            Translation()
        }
    }

    suspend fun updateVerse(id: Int, bookmarked: Boolean) {
        try {
            coroutineScope { async { gitaDataBase.gitaDAO.updateVerse(id, bookmarked) }.await() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getBookmark(): ResultOf<List<VerseItemDB>> {
        return try {
            val result = coroutineScope { async { gitaDataBase.gitaDAO.getBookmark() }.await() }
            ResultOf.Success(result)
        } catch (e: Exception) {
            ResultOf.Failure(e)
        }
    }
}
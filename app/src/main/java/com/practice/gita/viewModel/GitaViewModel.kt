package com.practice.gita.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.gita.data.ChapterSummaryItem
import com.practice.gita.data.VerseForUI
import com.practice.gita.data.VerseItemDB
import com.practice.gita.repository.GitaRepository
import com.practice.gita.utils.ResultOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GitaViewModel(
    private val repository: GitaRepository
) :
    ViewModel() {

    private val _chapterSummaryListLiveData =
        MutableLiveData<ResultOf<List<ChapterSummaryItem>>>(ResultOf.Loading)
    val chapterSummaryListLiveData: LiveData<ResultOf<List<ChapterSummaryItem>>> =
        _chapterSummaryListLiveData

    private val _verseLiveData =
        MutableLiveData<ResultOf<VerseForUI>>(ResultOf.Loading)
    val verseLiveData: LiveData<ResultOf<VerseForUI>> = _verseLiveData

    private val _bookmarkLiveData =
        MutableLiveData<ResultOf<List<VerseItemDB>>>(ResultOf.Loading)
    val bookmarkLiveData: LiveData<ResultOf<List<VerseItemDB>>> = _bookmarkLiveData

    var chapter: Int = 1
    var verse: Int = 1
    var maxVerse: Int = 20
    lateinit var chapterSummaryItem: ChapterSummaryItem
    fun getChapterSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            if (chapterSummaryListLiveData.value !is ResultOf.Success) {
                _chapterSummaryListLiveData.postValue(ResultOf.Loading)
                _chapterSummaryListLiveData.postValue(repository.getChapterSummary())
            }
        }
    }

    fun getVerse(
        comSource: String,
        tranSource: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getVerseFromDatabase(
                chapter,
                verse,
                comSource,
                tranSource
            )
            if (result !is ResultOf.Failure)
                _verseLiveData.postValue(result)
            else {
                viewModelScope.launch(Dispatchers.IO) {
                    _verseLiveData.postValue(
                        repository.getSingleVerseFromInternet(
                            chapter,
                            verse,
                            comSource,
                            tranSource
                        )
                    )
                }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.getVersesFromInternet(chapter)
                }
            }
        }
    }

    fun loadingState() {
        _verseLiveData.postValue(ResultOf.Loading)
    }

    fun updateVerse(id: Int, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.updateVerse(id, isBookmarked)
        }
    }

    fun getBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.getBookmark()
            _bookmarkLiveData.postValue(res)
        }
    }
}
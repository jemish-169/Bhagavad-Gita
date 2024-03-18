package com.practice.gita.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practice.gita.repository.GitaRepository

class GitaViewModelFactory(
    private val repository: GitaRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GitaViewModel(repository) as T
    }
}
package com.practice.gita.utils

sealed class ResultOf<out T> {
    data class Success<out R>(val value: R) : ResultOf<R>()
    data class Failure(val exception: Exception) : ResultOf<Nothing>()
    data object Loading : ResultOf<Nothing>()
}
package com.practice.gita

import android.app.Application
import com.practice.gita.api.ApiService
import com.practice.gita.api.RetrofitHelper
import com.practice.gita.repository.GitaRepository
import com.practice.gita.db.GitaDataBase

class GitaApplication : Application() {

    lateinit var gitaRepository: GitaRepository
    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        val gitaDataBase = GitaDataBase.getDataBase(applicationContext)
        gitaRepository = GitaRepository(apiService, gitaDataBase, applicationContext)
    }
}
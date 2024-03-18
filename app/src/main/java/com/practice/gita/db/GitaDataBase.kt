package com.practice.gita.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.practice.gita.data.ChapterSummaryItem
import com.practice.gita.data.Commentary
import com.practice.gita.data.Translation
import com.practice.gita.data.VerseItemDB
import com.practice.gita.utils.Constants.Companion.DATABASE_NAME

@Database(
    entities = [ChapterSummaryItem::class, VerseItemDB::class, Commentary::class, Translation::class],
    version = 1,
    exportSchema = false
)
abstract class GitaDataBase : RoomDatabase() {

    abstract val gitaDAO: GitaDAO

    companion object {
        @Volatile
        private var INSTANCE: GitaDataBase? = null

        fun getDataBase(context: Context): GitaDataBase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        GitaDataBase::class.java,
                        DATABASE_NAME
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}
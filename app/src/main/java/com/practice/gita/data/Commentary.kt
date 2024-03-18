package com.practice.gita.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commentary")
data class Commentary(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val author_name: String = "",
    val description: String = "",
)
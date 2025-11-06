package com.reynaldi.posttestpertemuan5

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_table")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val caption: String,
    val imageUri: String,
    val profileImageResId: Int
)
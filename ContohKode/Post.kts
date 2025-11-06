package com.pmob.postapp

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitas untuk tabel 'post' di database Room.
 * Ini akan menyimpan semua informasi untuk satu postingan.
 */
@Entity(tableName = "post_table")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val caption: String,
    // Kita simpan URI gambar sebagai String.
    val imageUri: String,
    // Kita akan gunakan ID statis untuk gambar profil (dari drawable)
    val profileImageResId: Int
)

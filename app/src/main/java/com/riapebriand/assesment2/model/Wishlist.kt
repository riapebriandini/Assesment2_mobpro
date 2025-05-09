package com.riapebriand.assesment2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist")
data class Wishlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nama: String,
    val deksripsi: String,
    val kategori: String,
    val isTercapai: Boolean = false,
    val isHapus: Boolean = false,
    val deletedAt: String? = null
)